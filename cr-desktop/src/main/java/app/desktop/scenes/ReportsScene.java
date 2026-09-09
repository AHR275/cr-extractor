package app.desktop.scenes;

import app.desktop.components.ReportDetailsComponent;
import app.desktop.components.ReportsCardsComponent;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;

public class ReportsScene extends BorderPane {

    private final ReportsCardsComponent reportsCards;
    private final ReportDetailsComponent reportDetails;

    public ReportsScene() {

        getStyleClass().add("page");

        reportsCards =
                new ReportsCardsComponent();

        reportDetails =
                new ReportDetailsComponent();

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
}