package app.desktop.components;

import app.desktop.controllers.NavigationController;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class NavBar extends HBox {

    private final NavigationController navigation;

    public NavBar(StackPane content) {

        navigation = new NavigationController(content);

        Button extraction = new Button("Extract");
        Button reports = new Button("Reports");

        // CSS classes
        getStyleClass().add("navbar");

        extraction.getStyleClass().add("navbar-button");
        reports.getStyleClass().add("navbar-button");

        // Navigation
        extraction.setOnAction(
            e -> navigation.showExtraction()
        );

        reports.setOnAction(
            e -> navigation.showReports()
        );

        getChildren().addAll(
            extraction,
            reports
        );
    }
}