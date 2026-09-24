package gamy.controllers;

import gamy.daos.AbstractDAO;
import gamy.daos.UserDAO;
import gamy.models.GamePlatform;
import gamy.models.Genre;
import gamy.models.RandomResult;
import gamy.models.User;
import gamy.services.RandomizerService;
import gamy.utils.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class RandomizerController {

    @FXML private RadioButton radioSolo;
    @FXML private RadioButton radioMulti;
    @FXML private ListView<String> listGenres;
    @FXML private ListView<String> listPlatforms;
    
    @FXML private ListView<User> listFriends; 
    @FXML private VBox boxFriends;
    @FXML private ProgressIndicator loadingIndicator;
    
    @FXML private VBox resultBox;
    @FXML private Label lblGameTitle;
    @FXML private Label lblPriceInfo;

    private Long currentUserId = SessionManager.getCurrentUser().getId();
    private final UserDAO userDAO = new UserDAO();
    
    private final RandomizerService randomizerService = new RandomizerService();
    
    private final AbstractDAO<Genre> genreDAO = new AbstractDAO<>(Genre.class) {};
    private final AbstractDAO<GamePlatform> platformDAO = new AbstractDAO<>(GamePlatform.class) {};

    @FXML
    public void initialize() {
        setupListViews();
        loadDataAsync();

        radioMulti.selectedProperty().addListener((obs, oldVal, newVal) -> {
            boxFriends.setDisable(!newVal);
            if (!newVal) listFriends.getSelectionModel().clearSelection();
        });
    }

    private void setupListViews() {
        listGenres.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listPlatforms.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listFriends.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        listFriends.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                setText(empty || user == null ? null : user.getPseudo());
            }
        });
    }

    private void loadDataAsync() {
        loadingIndicator.setVisible(true);

        CompletableFuture.supplyAsync(() -> {
            List<String> genres = genreDAO.findAll().stream().map(Genre::getName).collect(Collectors.toList());
            List<String> platforms = platformDAO.findAll().stream().map(GamePlatform::getName).collect(Collectors.toList());
            
            User currentUser = userDAO.findByIdWithRelations(currentUserId);
            List<User> friends = List.copyOf(currentUser.getFriends());

            return new Object[]{genres, platforms, friends};
        }).thenAccept(result -> Platform.runLater(() -> {
            @SuppressWarnings("unchecked")
            List<String> loadedGenres = (List<String>) result[0];
            @SuppressWarnings("unchecked")
            List<String> loadedPlatforms = (List<String>) result[1];
            @SuppressWarnings("unchecked")
            List<User> loadedFriends = (List<User>) result[2];

            listGenres.getItems().setAll(loadedGenres);
            listPlatforms.getItems().setAll(loadedPlatforms);
            listFriends.getItems().setAll(loadedFriends);
            loadingIndicator.setVisible(false);
        })).exceptionally(ex -> {
            Platform.runLater(() -> {
                loadingIndicator.setVisible(false);
            });
            return null;
        });
    }

    @FXML private void selectAllGenres() { listGenres.getSelectionModel().selectAll(); }
    @FXML private void clearGenres() { listGenres.getSelectionModel().clearSelection(); }

    @FXML private void selectAllPlatforms() { listPlatforms.getSelectionModel().selectAll(); }
    @FXML private void clearPlatforms() { listPlatforms.getSelectionModel().clearSelection(); }

    @FXML private void selectAllFriends() { listFriends.getSelectionModel().selectAll(); }
    @FXML private void clearFriends() { listFriends.getSelectionModel().clearSelection(); }

    @FXML
    private void handleRandomize() {
        loadingIndicator.setVisible(true);
        resultBox.setVisible(false);
        
        boolean isSolo = radioSolo.isSelected();
        List<String> selectedGenres = listGenres.getSelectionModel().getSelectedItems();
        List<String> selectedPlatforms = listPlatforms.getSelectionModel().getSelectedItems();
        
        List<Long> participantIds = listFriends.getSelectionModel().getSelectedItems()
            .stream()
            .map(User::getId)
            .collect(Collectors.toList());
        
        CompletableFuture.supplyAsync(() -> {
            try {
                if (isSolo) {
                    return randomizerService.randomizeSolo(currentUserId, selectedGenres, selectedPlatforms, null);
                } else {
                    participantIds.add(currentUserId);
                    return randomizerService.randomizeMulti(participantIds, selectedGenres, selectedPlatforms, null, null);
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }).thenAccept(result -> {
            Platform.runLater(() -> displayResult(result));
        }).exceptionally(ex -> {
            Platform.runLater(() -> showError(ex.getCause().getMessage()));
            return null;
        });
    }

    private void displayResult(RandomResult result) {
        loadingIndicator.setVisible(false);
        lblGameTitle.setText(result.getGame().getTitle());
        
        if (result.isRequiresPurchase()) {
            lblPriceInfo.setText(String.format("Attention : Un ou plusieurs membres doivent acheter ce jeu (Prix estimé : %.2f €)", result.getPriceToPay()));
            lblPriceInfo.setVisible(true);
        } else {
            lblPriceInfo.setVisible(false);
        }
        
        resultBox.setVisible(true);
    }

    private void showError(String message) {
        loadingIndicator.setVisible(false);
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Tirage impossible");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}