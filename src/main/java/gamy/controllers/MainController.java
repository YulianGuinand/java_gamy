package gamy.controllers;

import gamy.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class MainController {

    @FXML private Label userNameLabel;
    @FXML private StackPane contentArea;

    @FXML
    public void initialize() {
        userNameLabel.setText("Bonjour !");
    }

    @FXML
    public void showLibrary() {
        System.out.println("Affichage de la bibliothèque...");
    }

    @FXML
    public void showFriends() {
        System.out.println("Affichage des amis...");
    }

    @FXML
    public void showDiscovery() {
        System.out.println("Affichage de la découverte...");
    }

    @FXML
    public void showRandomizer() {
        System.out.println("Affichage du randomizer...");
    }

    @FXML
    public void handleLogout() {
        System.out.println("Déconnexion...");
        SceneManager.switchScene("Login.fxml", "Connexion");
    }
}