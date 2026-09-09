package app.desktop.scenes;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import app.desktop.components.NavBar;

public class MainScene extends BorderPane {

    private final StackPane content;
    private final NavBar navBar;

   public MainScene() {

    content = new StackPane();

    content.getChildren().add(
        new ExtractionScene()
    );

    navBar = new NavBar(content);

    setCenter(content);
    setBottom(navBar);
}
    public void showExtraction() {
        content.getChildren().setAll(
            new ExtractionScene()
        );
    }

    public void showReports() {
        content.getChildren().setAll(
            new ReportsScene()
        );
    }
}