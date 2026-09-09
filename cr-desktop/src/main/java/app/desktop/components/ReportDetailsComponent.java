
package app.desktop.components;

import java.util.Map;

import app.core.models.CRField;
import app.core.models.CRReport;
import app.core.models.CRSection;
import app.core.models.SummaryCRReport;
import app.core.services.CRRepoertService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class ReportDetailsComponent extends BorderPane {

    private SummaryCRReport summaryReport;
    private CRReport report;

    private final CRRepoertService crRepoertService = new CRRepoertService();

    private final VBox content;

    public ReportDetailsComponent() {

        getStyleClass().add("report-details");

   
        content = new VBox(20);
        content.setPadding(new Insets(20));

        ScrollPane scrollPane =
                new ScrollPane(content);

        scrollPane.setFitToWidth(true);

        setCenter(scrollPane);
    }

    public void setReport(SummaryCRReport summaryReport) {

        this.summaryReport = summaryReport;

        this.report =
                crRepoertService.getReport(summaryReport);

        showReport();
    }

    private void showReport() {

        content.getChildren().clear();

        if (report == null) {
            return;
        }

        CRSection data =
                report.getData();

        renderSection(
                data,
                content
        );
    }

    private void renderSection(
            CRSection section,
            VBox parent
    ) {

        // -------------------------
        // Fields
        // -------------------------

        for (Map.Entry<String, CRField> entry :
                section.getFields().entrySet()) {

            CRField field =
                    entry.getValue();

            VBox fieldBox =
                    createField(
                            field
                    );

            parent.getChildren().add(
                    fieldBox
            );
        }

        // -------------------------
        // Nested sections
        // -------------------------

        for (Map.Entry<String, CRSection> entry :
                section.getSections().entrySet()) {

            String sectionName =
                    entry.getKey();

            CRSection child =
                    entry.getValue();

            VBox sectionBox =
                    createSection(
                            sectionName
                    );

            parent.getChildren().add(
                    sectionBox
            );

            VBox sectionContent =
                    new VBox(10);

            sectionContent.setPadding(
                    new Insets(10)
            );

            sectionBox.getChildren().add(
                    sectionContent
            );

            renderSection(
                    child,
                    sectionContent
            );
        }
    }

    private VBox createSection(
            String name
    ) {

        VBox box =
                new VBox(8);

        box.getStyleClass().add(
                "report-section"
        );

        Label title =
                new Label(
                        formatSectionName(name)
                );

        title.getStyleClass().add(
                "report-section-title"
        );

        box.getChildren().add(
                title
        );

        return box;
    }
private VBox createField(CRField field) {

    VBox box = new VBox(3);

    String[] labels = field.getLabels();
    Object[] values = field.getValue();

    box.getStyleClass().add("report-field");

    // -------------------------
    // English
    // -------------------------

    Label enLabel =
            new Label(labels[0]);

    enLabel.getStyleClass().add(
            "report-field-label"
    );

    ClickableLabel enValue =
            new ClickableLabel(
                    values[0] == null
                            ? "-"
                            : cleanValue(values[0])
            );

    enValue.getStyleClass().add(
            "report-field-value"
    );

    enValue.setWrapText(true);


    // -------------------------
    // Arabic
    // -------------------------

    Label arLabel =
            new Label(labels[1]);

    arLabel.getStyleClass().add(
            "report-field-label"
    );

    ClickableLabel arValue =
            new ClickableLabel(
                    values[1] == null
                            ? "-"
                            : cleanValue(values[1])
            );

    arValue.getStyleClass().add(
            "report-field-value"
    );

    arValue.setWrapText(true);

    arLabel.setAlignment(Pos.CENTER_RIGHT);
    arValue.setAlignment(Pos.CENTER_RIGHT);


    // -------------------------
    // English side
    // -------------------------

    VBox english =
            new VBox(3);

    english.getChildren().addAll(
            enLabel,
            enValue
    );


    // -------------------------
    // Arabic side
    // -------------------------

    VBox arabic =
            new VBox(3);

    arabic.setAlignment(Pos.TOP_RIGHT);

    arabic.getChildren().addAll(
            arLabel,
            arValue
    );


    // -------------------------
    // Two columns
    // -------------------------

    GridPane row =
            new GridPane();

    row.setHgap(20);

    row.add(
            english,
            0,
            0
    );

    row.add(
            arabic,
            1,
            0
    );

    GridPane.setFillWidth(
            english,
            true
    );

    GridPane.setFillWidth(
            arabic,
            true
    );

    box.getChildren().add(row);

    return box;
}
    private String formatSectionName(
            String name
    ) {

        String result =
                name.replaceAll(
                        "([a-z])([A-Z])",
                        "$1 $2"
                );

        result =
                Character.toUpperCase(
                        result.charAt(0)
                )
                + result.substring(1);

        result =
                result.replaceAll(
                        "\\bCr\\b",
                        "CR"
                );

        result =
                result.replaceAll(
                        "\\bId\\b",
                        "ID"
                );

        return result;
    }

    private String cleanValue(Object value) {
    if (value == null) {
        return "-";
    }

    String text = String.valueOf(value);

    if (text.length() >= 2 &&
        text.startsWith("\"") &&
        text.endsWith("\"")) {

        text = text.substring(1, text.length() - 1);
    }

    return text;
}

    public SummaryCRReport getSummaryReport() {
        return summaryReport;
    }

    public CRReport getReport() {
        return report;
    }
}

