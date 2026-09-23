package gamy.controllers;

import gamy.daos.UserDAO;
import gamy.models.User;
import gamy.services.UserService;
import gamy.utils.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FriendsController {

    @FXML private TextField searchField;
    @FXML private ListView<User> searchResultListView;
    @FXML private ListView<User> friendsListView;
    @FXML private ListView<User> blockedListView;
    @FXML private Label feedbackLabel;

    private final UserDAO userDAO = new UserDAO();
    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        Callback<ListView<User>, ListCell<User>> cellFactory = param -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null || user.getPseudo() == null) {
                    setText(null);
                } else {
                    setText(user.getPseudo());
                }
            }
        };

        searchResultListView.setCellFactory(cellFactory);
        friendsListView.setCellFactory(cellFactory);
        blockedListView.setCellFactory(cellFactory);

        loadData();
    }

    private void loadData() {
        Long userId = SessionManager.getCurrentUser().getId();

        CompletableFuture.runAsync(() -> {
            User userWithRelations = userDAO.findByIdWithRelations(userId);
            List<User> friends = (userWithRelations != null && userWithRelations.getFriends() != null) 
                    ? new ArrayList<>(userWithRelations.getFriends()) : new ArrayList<>();
            List<User> blocked = (userWithRelations != null && userWithRelations.getBlockedUsers() != null) 
                    ? new ArrayList<>(userWithRelations.getBlockedUsers()) : new ArrayList<>();
            
            Platform.runLater(() -> {
                friendsListView.setItems(FXCollections.observableArrayList(friends));
                blockedListView.setItems(FXCollections.observableArrayList(blocked));
            });
        });
    }

    @FXML
    public void handleSearchUser() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText("Veuillez entrer un terme de recherche.");
            return;
        }

        feedbackLabel.setStyle("-fx-text-fill: blue;");
        feedbackLabel.setText("Recherche en cours...");

        CompletableFuture.runAsync(() -> {
            User foundUser = userDAO.findByPseudo(query);
            List<User> results = new ArrayList<>();
            if (foundUser != null && !foundUser.getId().equals(SessionManager.getCurrentUser().getId())) {
                results.add(foundUser);
            }

            Platform.runLater(() -> {
                searchResultListView.setItems(FXCollections.observableArrayList(results));
                if (results.isEmpty()) {
                    feedbackLabel.setStyle("-fx-text-fill: red;");
                    feedbackLabel.setText("Aucun utilisateur trouvé.");
                } else {
                    feedbackLabel.setText("");
                }
            });
        });
    }

    @FXML
    public void handleAddSearchedUser() {
        User selectedUser = searchResultListView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText("Veuillez sélectionner un utilisateur dans les résultats de recherche.");
            return;
        }

        Long currentUserId = SessionManager.getCurrentUser().getId();
        feedbackLabel.setStyle("-fx-text-fill: blue;");
        feedbackLabel.setText("Ajout en cours...");

        CompletableFuture.runAsync(() -> {
            try {
                userService.addFriend(currentUserId, selectedUser.getId());

                Platform.runLater(() -> {
                    feedbackLabel.setStyle("-fx-text-fill: green;");
                    feedbackLabel.setText("Ami ajouté avec succès !");
                    searchResultListView.getItems().clear();
                    searchField.clear();
                    loadData();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    feedbackLabel.setStyle("-fx-text-fill: red;");
                    feedbackLabel.setText(e.getMessage());
                });
            }
        });
    }

    @FXML
    public void handleRemoveFriend() {
        User selectedFriend = friendsListView.getSelectionModel().getSelectedItem();
        if (selectedFriend == null) {
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText("Veuillez sélectionner un ami dans votre liste.");
            return;
        }

        Long currentUserId = SessionManager.getCurrentUser().getId();
        feedbackLabel.setStyle("-fx-text-fill: blue;");
        feedbackLabel.setText("Suppression en cours...");

        CompletableFuture.runAsync(() -> {
            try {
                userService.removeFriend(currentUserId, selectedFriend.getId());

                Platform.runLater(() -> {
                    feedbackLabel.setStyle("-fx-text-fill: green;");
                    feedbackLabel.setText("Ami supprimé avec succès.");
                    loadData();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    feedbackLabel.setStyle("-fx-text-fill: red;");
                    feedbackLabel.setText(e.getMessage());
                });
            }
        });
    }

    @FXML
    public void handleBlockUser() {
        User selectedFriend = friendsListView.getSelectionModel().getSelectedItem();
        if (selectedFriend == null) {
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText("Veuillez sélectionner un utilisateur à bloquer.");
            return;
        }

        Long currentUserId = SessionManager.getCurrentUser().getId();
        feedbackLabel.setStyle("-fx-text-fill: blue;");
        feedbackLabel.setText("Blocage en cours...");

        CompletableFuture.runAsync(() -> {
            try {
                userService.blockUser(currentUserId, selectedFriend.getId());

                Platform.runLater(() -> {
                    feedbackLabel.setStyle("-fx-text-fill: green;");
                    feedbackLabel.setText("Utilisateur bloqué.");
                    loadData();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    feedbackLabel.setStyle("-fx-text-fill: red;");
                    feedbackLabel.setText(e.getMessage());
                });
            }
        });
    }

    @FXML
    public void handleUnblockUser() {
        User selectedBlockedUser = blockedListView.getSelectionModel().getSelectedItem();
        if (selectedBlockedUser == null) {
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText("Veuillez sélectionner un utilisateur à débloquer.");
            return;
        }

        Long currentUserId = SessionManager.getCurrentUser().getId();
        feedbackLabel.setStyle("-fx-text-fill: blue;");
        feedbackLabel.setText("Déblocage en cours...");

        CompletableFuture.runAsync(() -> {
            try {
                userService.unblockUser(currentUserId, selectedBlockedUser.getId());

                Platform.runLater(() -> {
                    feedbackLabel.setStyle("-fx-text-fill: green;");
                    feedbackLabel.setText("Utilisateur débloqué avec succès.");
                    loadData();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    feedbackLabel.setStyle("-fx-text-fill: red;");
                    feedbackLabel.setText(e.getMessage());
                });
            }
        });
    }
}