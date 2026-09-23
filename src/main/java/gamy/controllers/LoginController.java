package gamy.controllers;

import java.util.concurrent.CompletableFuture;

import gamy.models.User;
import gamy.services.AuthService;
import gamy.utils.SceneManager;
import gamy.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.application.Platform;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

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

        errorLabel.setStyle("-fx-text-fill: blue;");
        errorLabel.setText("Connexion en cours...");

        CompletableFuture.runAsync(() -> {
            try {
                User user = authService.login(email, password);
                SessionManager.setCurrentUser(user);

                Platform.runLater(() -> {
                    System.out.println("Connexion réussie pour : " + email);
                    SceneManager.switchScene("MainLayout.fxml", "Accueil");
                });
                
            } catch (Exception e) {
                Platform.runLater(() -> {
                    errorLabel.setStyle("-fx-text-fill: red;");
                    errorLabel.setText(e.getMessage());
                });
            }
        });
    }

    @FXML
    public void goToRegister() {
        gamy.utils.SceneManager.switchScene("Register.fxml", "Inscription");
    }
}