package gamy.controllers;

import gamy.daos.LibraryDAO;
import gamy.models.GameMode;
import gamy.models.LibraryEntry;
import gamy.models.LibraryStatus;
import gamy.services.LibraryService;
import gamy.utils.SessionManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.concurrent.CompletableFuture;

public class LibraryController {

    @FXML private TableView<LibraryEntry> libraryTable;
    @FXML private TableColumn<LibraryEntry, String> titleColumn;
    @FXML private TableColumn<LibraryEntry, String> modeColumn;
    @FXML private TableColumn<LibraryEntry, String> priceColumn;
    @FXML private TableColumn<LibraryEntry, String> statusColumn;
    @FXML private TableColumn<LibraryEntry, String> descriptionColumn;
    @FXML private Label feedbackLabel;

    @FXML private ComboBox<LibraryStatus> editStatusComboBox;
    @FXML private ComboBox<GameMode> editModeComboBox;
    @FXML private TextField editPriceField;
    @FXML private TextArea editDescriptionArea; // Nouveau champ

    private final LibraryService libraryService = new LibraryService();
    private final LibraryDAO libraryDAO = new LibraryDAO();

    @FXML
    public void initialize() {
        editStatusComboBox.setItems(FXCollections.observableArrayList(LibraryStatus.values()));
        editModeComboBox.setItems(FXCollections.observableArrayList(GameMode.values()));

        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGame().getTitle()));
        modeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGame().getGameMode().name()));
        priceColumn.setCellValueFactory(cellData -> {
            Double price = cellData.getValue().getGame().getPrice();
            return new SimpleStringProperty(price != null ? price + " €" : "-");
        });
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().name()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGame().getDescription()));

        libraryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                editStatusComboBox.setValue(newVal.getStatus());
                if (newVal.getGame() != null) {
                    editModeComboBox.setValue(newVal.getGame().getGameMode());
                    editPriceField.setText(newVal.getGame().getPrice() != null ? String.valueOf(newVal.getGame().getPrice()) : "");
                    editDescriptionArea.setText(newVal.getGame().getDescription() != null ? newVal.getGame().getDescription() : "");
                }
            }
        });

        loadLibraryData();
    }

    private void loadLibraryData() {
        Long userId = SessionManager.getCurrentUser().getId();
        CompletableFuture.supplyAsync(() -> libraryService.getUserLibrary(userId))
            .thenAcceptAsync(entries -> {
                ObservableList<LibraryEntry> observableEntries = FXCollections.observableArrayList(entries);
                Platform.runLater(() -> libraryTable.setItems(observableEntries));
            });
    }

    @FXML
    private void handleUpdateEntry() {
        LibraryEntry selectedEntry = libraryTable.getSelectionModel().getSelectedItem();
        if (selectedEntry == null) {
            feedbackLabel.setText("Veuillez sélectionner un jeu à modifier.");
            return;
        }

        selectedEntry.setStatus(editStatusComboBox.getValue());

        if (selectedEntry.getGame() != null) {
            selectedEntry.getGame().setGameMode(editModeComboBox.getValue());
            try {
                String priceText = editPriceField.getText().trim();
                selectedEntry.getGame().setPrice(priceText.isEmpty() ? null : Double.parseDouble(priceText));
            } catch (NumberFormatException e) {
                feedbackLabel.setStyle("-fx-text-fill: red;");
                feedbackLabel.setText("Format de prix invalide (ex: 19.99).");
                return;
            }
            selectedEntry.getGame().setDescription(editDescriptionArea.getText());
        }

        libraryDAO.updateEntryAndGame(selectedEntry);

        feedbackLabel.setStyle("-fx-text-fill: green;");
        feedbackLabel.setText("Jeu mis à jour avec succès !");
        loadLibraryData();
        libraryTable.refresh(); 
    }

    @FXML
    private void handleDeleteEntry() {
        LibraryEntry selectedEntry = libraryTable.getSelectionModel().getSelectedItem();
        if (selectedEntry == null) {
            feedbackLabel.setText("Veuillez sélectionner un jeu à supprimer.");
            return;
        }

        libraryDAO.delete(selectedEntry);
        feedbackLabel.setStyle("-fx-text-fill: green;");
        feedbackLabel.setText("Jeu supprimé de votre bibliothèque.");
        loadLibraryData();
    }
}