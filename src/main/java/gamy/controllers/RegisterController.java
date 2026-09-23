package gamy.controllers;

import java.util.concurrent.CompletableFuture;

import gamy.services.AuthService;
import gamy.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.application.Platform;

public class RegisterController {

    @FXML private TextField emailField;
    @FXML private TextField pseudoField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;

    private AuthService authService;

    public RegisterController() {
        this.authService = new AuthService();
    }

    @FXML
    public void handleRegister() {
        String email = emailField.getText();
        String pseudo = pseudoField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (email.isEmpty() || pseudo.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Les mots de passe ne correspondent pas.");
            return;
        }

        errorLabel.setStyle("-fx-text-fill: blue;");
        errorLabel.setText("Création du compte en cours...");

        CompletableFuture.runAsync(() -> {
            try {
                authService.register(email, password, pseudo);
                authService.login(email, password);
                
                Platform.runLater(() -> {
                    System.out.println("Compte créé et connecté : " + pseudo);
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
    public void backToLogin() {
        SceneManager.switchScene("Login.fxml", "Connexion");
    }
}