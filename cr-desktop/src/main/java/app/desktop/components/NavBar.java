package app.desktop.components;

import app.desktop.controllers.NavigationController;
import app.desktop.ctx.ControllersContexts;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class NavBar extends HBox {

    private final NavigationController navigation;
    private final ControllersContexts controllersContexts ; 

    public NavBar(StackPane content,ControllersContexts rc ) {
        this.controllersContexts = rc ; 
        navigation = new NavigationController(content , controllersContexts.getReportsController());

        Button extraction = new Button("Extract");
        Button reports = new Button("Reports");
        Button dashboard = new Button("Dashboard");

        // CSS classes
        getStyleClass().add("navbar");

        extraction.getStyleClass().add("navbar-button");
        reports.getStyleClass().add("navbar-button");
        dashboard.getStyleClass().add("navbar-button");

        // Navigation
        extraction.setOnAction(
            e -> navigation.showExtraction()
        );

        reports.setOnAction(
            e -> navigation.showReports()
        );
        dashboard.setOnAction(
            e -> navigation.showDashboard()
        );

        getChildren().addAll(
            dashboard,
            extraction,
            reports
        );
    }
}