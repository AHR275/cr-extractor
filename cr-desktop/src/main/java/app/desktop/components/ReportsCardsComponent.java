package app.desktop.components;

import java.util.List;
import java.util.function.Consumer;

import app.core.models.SummaryCRReport;
import app.core.services.CRRepoertService;
import app.desktop.controllers.ReportsController;
import app.desktop.ctx.ControllersContexts;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;


public class ReportsCardsComponent extends BorderPane {

    private final CRRepoertService reportService;
    private ReportsController reportsController; 

    private final FlowPane cards;

    private Consumer<SummaryCRReport> onReportSelected;

    public ReportsCardsComponent(ControllersContexts CtrlCtx) {
        this.reportsController = CtrlCtx.getReportsController() ; 
        reportService =
                new CRRepoertService();


        cards = new FlowPane();

        cards.getStyleClass().add(
                "report-cards"
        );

        loadReports();

        ScrollPane scroll =
                new ScrollPane(cards);

        scroll.setFitToWidth(true);

        setCenter(scroll);
    }

    private void loadReports() {
    List<SummaryCRReport> reports =
            reportsController.getSummaryReports();

    for (SummaryCRReport report : reports) {
        CRReportCard card = new CRReportCard(report);

        card.setOnMouseClicked(e -> {
            // Save the selected report in the shared controller
            reportsController.setSelectedReport(report);

            

            // Notify ReportsScene to update its details panel
            if (onReportSelected != null) {
                onReportSelected.accept(report);
            }
        });

        cards.getChildren().add(card);
    }
}

    public void setOnReportSelected(
            Consumer<SummaryCRReport> callback
    ) {

        this.onReportSelected = callback;
    }
}