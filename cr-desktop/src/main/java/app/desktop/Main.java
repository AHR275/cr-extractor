package app.desktop;

import app.desktop.scenes.MainScene;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        MainScene mainScene = new MainScene();

        Scene scene = new Scene(
            mainScene,
            700,
            500
        );

        // Load global CSS
        scene.getStylesheets().add(
            getClass()
                .getResource("/css/main.css")
                .toExternalForm()
        );

        stage.setTitle("CR Extractor");

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}