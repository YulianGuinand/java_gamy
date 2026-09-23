package gamy.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SceneManager {

    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(String fxmlFileName, String title) {
        try {
            URL fxmlLocation = SceneManager.class.getResource("/views/" + fxmlFileName);
            if (fxmlLocation == null) {
                throw new IOException("Fichier FXML introuvable : " + fxmlFileName);
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            Scene scene = new Scene(root, 1024, 768);
            primaryStage.setTitle("Gamy - " + title);
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue : " + fxmlFileName);
            e.printStackTrace();
        }
    }
}