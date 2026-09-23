package gamy.controllers;

import gamy.models.LibraryEntry;
import gamy.services.LibraryService;
import gamy.utils.SessionManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.concurrent.CompletableFuture;

public class LibraryController {

    @FXML private TableView<LibraryEntry> libraryTable;
    @FXML private TableColumn<LibraryEntry, String> titleColumn;
    @FXML private TableColumn<LibraryEntry, String> modeColumn;
    @FXML private TableColumn<LibraryEntry, String> statusColumn;

    private final LibraryService libraryService = new LibraryService();

    @FXML
    public void initialize() {
        // Configuration des colonnes
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGame().getTitle()));
        modeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGame().getGameMode().name()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().name()));

        loadLibraryData();
    }

    private void loadLibraryData() {
        Long userId = SessionManager.getCurrentUser().getId();

        CompletableFuture.supplyAsync(() -> {
            return libraryService.getUserLibrary(userId);
        }).thenAcceptAsync(entries -> {
            ObservableList<LibraryEntry> observableEntries = FXCollections.observableArrayList(entries);
            Platform.runLater(() -> libraryTable.setItems(observableEntries));
        });
    }
}