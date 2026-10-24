
package app.desktop.ctx;

import app.desktop.controllers.NavigationController;
import app.desktop.controllers.ReportsController;

public class ControllersContexts {

    private final ReportsController reportsController;
    private final NavigationController navigationController;

    public ControllersContexts(
        ReportsController reportsController, 
        NavigationController navigationController
    ) {
        this.reportsController = reportsController;
        this.navigationController = navigationController ; 
    }

    public ReportsController getReportsController() {
        return reportsController;
    }
    public NavigationController getNavigationController() {
        return navigationController;
    }
}
