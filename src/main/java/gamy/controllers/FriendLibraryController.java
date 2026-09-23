package gamy.controllers;

import gamy.models.LibraryEntry;
import gamy.models.User;
import gamy.services.LibraryService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class FriendLibraryController {

    @FXML private Label titleLabel;
    @FXML private TableView<LibraryEntry> gamesTableView;
    @FXML private TableColumn<LibraryEntry, String> gameTitleColumn;
    @FXML private TableColumn<LibraryEntry, String> modeColumn;
    @FXML private TableColumn<LibraryEntry, String> priceColumn;
    @FXML private TableColumn<LibraryEntry, String> statusColumn;
    @FXML private TableColumn<LibraryEntry, String> descriptionColumn;

    private final LibraryService libraryService = new LibraryService();

    @FXML
    public void initialize() {
        gameTitleColumn.setCellValueFactory(cellData -> {
            LibraryEntry entry = cellData.getValue();
            return new SimpleStringProperty(entry.getGame() != null ? entry.getGame().getTitle() : "Inconnu");
        });

        modeColumn.setCellValueFactory(cellData -> {
            LibraryEntry entry = cellData.getValue();
            return new SimpleStringProperty(entry.getGame() != null && entry.getGame().getGameMode() != null ? entry.getGame().getGameMode().name() : "-");
        });

        priceColumn.setCellValueFactory(cellData -> {
            LibraryEntry entry = cellData.getValue();
            Double price = entry.getGame() != null ? entry.getGame().getPrice() : null;
            return new SimpleStringProperty(price != null ? price + " €" : "-");
        });

        statusColumn.setCellValueFactory(cellData -> {
            LibraryEntry entry = cellData.getValue();
            return new SimpleStringProperty(entry.getStatus() != null ? entry.getStatus().name() : "");
        });

        descriptionColumn.setCellValueFactory(cellData -> {
            LibraryEntry entry = cellData.getValue();
            String desc = entry.getGame() != null ? entry.getGame().getDescription() : "";
            return new SimpleStringProperty(desc != null ? desc : "");
        });
    }

    public void loadFriendLibrary(User friend) {
        titleLabel.setText("Bibliothèque de " + friend.getPseudo());
        
        libraryService.getFriendLibraryAsync(friend.getId(),
            entries -> gamesTableView.getItems().setAll(entries),
            Throwable::printStackTrace
        );
    }
}