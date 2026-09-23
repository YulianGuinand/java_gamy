package gamy.controllers;

import gamy.daos.GameDAO;
import gamy.daos.LibraryDAO;
import gamy.models.Game;
import gamy.models.GameMode;
import gamy.models.LibraryEntry;
import gamy.models.LibraryStatus;
import gamy.models.User;
import gamy.services.RawgApiService;
import gamy.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class DiscoveryController {

    @FXML private TextField searchField;
    @FXML private ListView<Game> resultsListView;
    @FXML private ComboBox<LibraryStatus> statusComboBox;
    @FXML private Label feedbackLabel;

    @FXML private Label detailTitleLabel;
    @FXML private ComboBox<GameMode> detailModeComboBox;
    @FXML private TextField detailPriceField;
    @FXML private TextArea detailDescriptionArea;

    private final RawgApiService rawgApiService = new RawgApiService();
    private final GameDAO gameDAO = new GameDAO();
    private final LibraryDAO libraryDAO = new LibraryDAO();

    @FXML
    public void initialize() {
        statusComboBox.setItems(FXCollections.observableArrayList(LibraryStatus.values()));
        detailModeComboBox.setItems(FXCollections.observableArrayList(GameMode.values()));

        resultsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Game game, boolean empty) {
                super.updateItem(game, empty);
                setText(empty || game == null ? null : game.getTitle() + (game.getReleaseDate() != null ? " (" + game.getReleaseDate().getYear() + ")" : ""));
            }
        });

        resultsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                detailTitleLabel.setText(newValue.getTitle());
                detailModeComboBox.setValue(newValue.getGameMode() != null ? newValue.getGameMode() : GameMode.BOTH);
                detailPriceField.setText(newValue.getPrice() != null ? String.valueOf(newValue.getPrice()) : "");
                detailDescriptionArea.setText(newValue.getDescription() != null ? newValue.getDescription() : "");
            }
        });
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            feedbackLabel.setText("Veuillez entrer un terme de recherche.");
            return;
        }

        feedbackLabel.setText("Recherche en cours...");
        rawgApiService.searchGamesDetailedAsync(query,
            games -> {
                resultsListView.getItems().setAll(games);
                feedbackLabel.setText("");
            },
            error -> {
                feedbackLabel.setText("Erreur lors de la récupération des jeux.");
                error.printStackTrace();
            }
        );
    }

    @FXML
    private void handleAddToLibrary() {
        Game selectedGame = resultsListView.getSelectionModel().getSelectedItem();
        LibraryStatus selectedStatus = statusComboBox.getValue();
        User currentUser = SessionManager.getCurrentUser();

        if (selectedGame == null) {
            feedbackLabel.setText("Veuillez sélectionner un jeu dans la liste.");
            return;
        }
        if (selectedStatus == null) {
            feedbackLabel.setText("Veuillez choisir un statut.");
            return;
        }

        selectedGame.setGameMode(detailModeComboBox.getValue());
        try {
            String priceText = detailPriceField.getText().trim();
            selectedGame.setPrice(priceText.isEmpty() ? null : Double.parseDouble(priceText));
        } catch (NumberFormatException e) {
            feedbackLabel.setText("Format de prix invalide (ex: 19.99).");
            return;
        }
        selectedGame.setDescription(detailDescriptionArea.getText());

        Game managedGame = gameDAO.saveOrUpdateGame(selectedGame);
        if (managedGame == null || managedGame.getId() == null) {
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText("Erreur lors de l'enregistrement du jeu en base.");
            return;
        }

        LibraryEntry entry = new LibraryEntry(currentUser, managedGame, selectedStatus);

        try {
            libraryDAO.save(entry);
            feedbackLabel.setStyle("-fx-text-fill: green;");
            feedbackLabel.setText("Jeu modifié et ajouté avec succès !");
        } catch (Exception e) {
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText("Erreur : Ce jeu est déjà dans votre bibliothèque.");
            e.printStackTrace();
        }
    }
}