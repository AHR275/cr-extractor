package app.desktop.components;

// package app.desktop.components;

import app.core.models.SummaryCRReport;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CRReportCard extends VBox {

    private final SummaryCRReport report;

    public CRReportCard(SummaryCRReport report) {

        this.report = report;

        setSpacing(8);
        setPadding(new Insets(15));
        setAlignment(Pos.TOP_LEFT);

        getStyleClass().add("report-card");

        Label name =
                new Label(report.getEntityFullName());

        name.getStyleClass().add(
                "report-card-title"
        );

        Label crNumber =
                new Label(
                        "CR: " + report.getCrNumber()
                );

        crNumber.getStyleClass().add(
                "report-card-info"
        );

   

        Label status =
                new Label(
                        "Status: " + report.getStatus()
                );

        status.getStyleClass().add(
                "report-card-status"
        );

        getChildren().addAll(
                name,
                crNumber,
                
                status
        );
    }

    public SummaryCRReport getReport() {
        return report;
    }
}
