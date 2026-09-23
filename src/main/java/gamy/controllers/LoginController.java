package gamy.controllers;

import gamy.services.AuthService;
import gamy.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private AuthService authService;

    public LoginController() {
        this.authService = new AuthService();
    }

    @FXML
    public void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        try {
            authService.login(email, password);
            System.out.println("Connexion reussie pour : " + email);
            SceneManager.switchScene("MainLayout.fxml", "Accueil");
            
        } catch (Exception e) {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText(e.getMessage());
        }
    }
}