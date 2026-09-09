package app.desktop.components;

import app.core.models.SummaryCRReport;
import app.core.services.CRRepoertService;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;


public class ReportsCardsComponent extends BorderPane {

    private final CRRepoertService reportService;

    private final FlowPane cards;

    private Consumer<SummaryCRReport> onReportSelected;

    public ReportsCardsComponent() {

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
                reportService.getReports();

        for (SummaryCRReport report : reports) {

            CRReportCard card =
                    new CRReportCard(report);

            card.setOnMouseClicked(
                    e -> {

                        if (onReportSelected != null) {

                            onReportSelected.accept(
                                    report
                            );
                        }
                    }
            );

            cards.getChildren().add(card);
        }
    }

    public void setOnReportSelected(
            Consumer<SummaryCRReport> callback
    ) {

        this.onReportSelected = callback;
    }
}