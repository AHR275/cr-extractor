package app.desktop.controllers;
import javafx.scene.layout.StackPane; 
import app.desktop.scenes.ExtractionScene; 
import app.desktop.scenes.ReportsScene; 

public class NavigationController {

    private final StackPane content;

    public NavigationController(StackPane content) {
        this.content = content;
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