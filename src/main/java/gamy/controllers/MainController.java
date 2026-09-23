package gamy.controllers;

import java.io.IOException;

import gamy.models.User;
import gamy.utils.SceneManager;
import gamy.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import java.net.URL;
import javafx.scene.Node;

public class MainController {

    @FXML private Label userNameLabel;
    @FXML private StackPane contentArea;

    @FXML
    public void initialize() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            userNameLabel.setText("Bonjour, " + currentUser.getPseudo());
        }
        
        showLibrary();
    }

    private void loadView(String fxmlFileName) {
        try {
            URL location = getClass().getResource("/views/" + fxmlFileName);
            if (location != null) {
                Node view = FXMLLoader.load(location);
                contentArea.getChildren().setAll(view);
            }
        } catch (IOException e) {
            System.err.println("Erreur de chargement de la vue centrale : " + fxmlFileName);
            e.printStackTrace();
        }
    }

    @FXML
    public void showLibrary() {
        loadView("Library.fxml");
    }

    @FXML
    public void showFriends() {
        loadView("Friends.fxml");
    }

    @FXML
    public void showDiscovery() {
        // loadView("Discovery.fxml");
    }

    @FXML
    public void showRandomizer() {
        // loadView("Randomizer.fxml");
    }

    @FXML
    public void handleLogout() {
        SessionManager.logout();
        SceneManager.switchScene("Login.fxml", "Connexion");
    }
}