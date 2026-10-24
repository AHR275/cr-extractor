
package app.desktop.scenes;

import app.desktop.components.NavBar;
import app.desktop.controllers.ReportsController;
import app.desktop.controllers.NavigationController;
import app.desktop.ctx.ControllersContexts; 
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class MainScene extends BorderPane {

    private final StackPane content;
    private final NavBar navBar;

    // One shared controller for all scenes.
    private final ReportsController reportsController =
            new ReportsController();
    private  NavigationController navigationController ;

    private final ControllersContexts CtrlCtx = new ControllersContexts(
        reportsController,
         navigationController
        );

    public MainScene() {
        content = new StackPane();

        content.getChildren().add(
                new ExtractionScene()
        );

        navBar = new NavBar(content, CtrlCtx);

        setCenter(content);
        setBottom(navBar);
        navigationController = new NavigationController(content, reportsController);
    }

    public void showExtraction() {
        content.getChildren().setAll(
                new ExtractionScene()
        );
    }

    public void showReports() {
        content.getChildren().setAll(
                new ReportsScene(CtrlCtx)
        );
    }

    public void showDashboard() {
        content.getChildren().setAll(
                new DashboardScene(CtrlCtx)
        );
    }
}
