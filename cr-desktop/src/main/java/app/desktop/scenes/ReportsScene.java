package app.desktop.scenes;

import app.core.models.SummaryCRReport;
import app.desktop.components.ReportDetailsComponent;
import app.desktop.components.ReportsCardsComponent;
import app.desktop.controllers.ReportsController;
import app.desktop.ctx.ControllersContexts;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;

public class ReportsScene extends BorderPane {

    private final ReportsCardsComponent reportsCards;
    private final ReportDetailsComponent reportDetails;
    private final ReportsController reportsController ; 

    public ReportsScene(ControllersContexts CtrlCtx) {
        this.reportsController = CtrlCtx.getReportsController();
        getStyleClass().add("page");

        reportsCards =
                new ReportsCardsComponent(CtrlCtx);

        reportDetails =
                new ReportDetailsComponent(CtrlCtx);

 

        reportsCards.setOnReportSelected(
                report -> {
                    reportDetails.setReport(report);
                }
        );

        SplitPane content =
                new SplitPane();

        content.getItems().addAll(
                reportsCards,
                reportDetails
        );

        content.setDividerPositions(0.66);

        setCenter(content);
    }

        public void showReport(SummaryCRReport report) {
                reportsController.setSelectedReport(report);
                reportDetails.setReport(report);
        }

        
        public void showReport() {
                // reportsController.setSelectedReport(report);
                reportDetails.setReport(reportsController.getSelectedReport());
        }

        
}