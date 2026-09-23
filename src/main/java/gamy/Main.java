package gamy;

import gamy.utils.HibernateUtil;
import gamy.utils.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneManager.setPrimaryStage(primaryStage);
        
        SceneManager.switchScene("Login.fxml", "Connexion");
    }

    @Override
    public void stop() {
        System.out.println("Fermeture de l'application et d'Hibernate...");
        HibernateUtil.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}