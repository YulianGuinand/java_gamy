package gamy.controllers;

import gamy.models.LibraryEntry;
import gamy.models.User;
import gamy.services.LibraryService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class FriendLibraryController {

    @FXML private Label titleLabel;
    @FXML private TableView<LibraryEntry> gamesTableView;
    @FXML private TableColumn<LibraryEntry, String> gameTitleColumn;
    @FXML private TableColumn<LibraryEntry, String> platformColumn;
    @FXML private TableColumn<LibraryEntry, String> statusColumn;

    private final LibraryService libraryService = new LibraryService();

    @FXML
    public void initialize() {
        gameTitleColumn.setCellValueFactory(new PropertyValueFactory<>("gameTitle"));
        platformColumn.setCellValueFactory(new PropertyValueFactory<>("platformName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    public void loadFriendLibrary(User friend) {
        titleLabel.setText("Bibliothèque de " + friend.getPseudo());
        
        libraryService.getFriendLibraryAsync(friend.getId(),
            entries -> {
                gamesTableView.getItems().setAll(entries);
            },
            error -> {
                error.printStackTrace();
            }
        );
    }
}