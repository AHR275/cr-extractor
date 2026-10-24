
package app.desktop.controllers;

import java.util.HashMap;
import java.util.Map;

import app.desktop.ctx.ControllersContexts;
import app.desktop.scenes.DashboardScene;
import app.desktop.scenes.ExtractionScene;
import app.desktop.scenes.ReportsScene;
import javafx.scene.layout.BorderPane;
import  javafx.scene.layout.StackPane; 

public class NavigationController {

    private final StackPane content;
    private final ReportsController reportsController;

    private final ExtractionScene extractionScene;
    private final ReportsScene reportsScene;
    private final DashboardScene dashboardScene;
    private ControllersContexts CtrlCtx ; 

    public NavigationController(
            StackPane content,
            ReportsController reportsController) {

        this.content = content;
        this.reportsController = reportsController;

        this.CtrlCtx = new ControllersContexts(this.reportsController , this);


        // Create each scene once.

        this.extractionScene = new ExtractionScene();
        this.reportsScene = new ReportsScene(CtrlCtx);
        this.dashboardScene = new DashboardScene(CtrlCtx);

        // Set the initial view.
        showDashboard();
    }


    public Map<String,BorderPane> getScenes (){
        Map<String,BorderPane> scenes = new HashMap<>();
        scenes.put("dashboard",this.dashboardScene);
        scenes.put("reports",this.reportsScene);
        scenes.put("extraction",this.extractionScene);
        return scenes; 
    }

    public void showExtraction() {
        content.getChildren().setAll(extractionScene);
    }

    public void showReports() {
        content.getChildren().setAll(reportsScene);
        reportsScene.showReport();
        
    }

    public void showDashboard() {
        content.getChildren().setAll(dashboardScene);
    }
}
