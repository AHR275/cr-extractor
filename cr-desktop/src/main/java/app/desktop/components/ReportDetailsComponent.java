
package app.desktop.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.core.export.ExportFormat;
import app.core.models.CRField;
import app.core.models.CRReport;
import app.core.models.CRSection;
import app.core.models.SummaryCRReport;
import app.core.models.UploadedFile;
import app.desktop.controllers.ReportsController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import app.desktop.ctx.ControllersContexts;
public class ReportDetailsComponent extends BorderPane {

    private SummaryCRReport summaryReport;
    private List<CRReport> reports = new ArrayList<>();
    private CRReport currentReport;

    private final ReportsController reportsController ; 

    private final VBox content = new VBox(20);
    private final HBox actionBar = new HBox(10);
    private final HBox versionBar = new HBox(10);
    private final HBox languageBar = new HBox(5);

    private Button englishButton;
    private Button arabicButton;

    private int fieldCount;
    private int sectionCount;

    private boolean english = true;

    public ReportDetailsComponent(ControllersContexts CtrlCtx ) {
        this.reportsController = CtrlCtx.getReportsController()  ; 
        getStyleClass().add("report-details");

        createHeader();
        createContent();
    }

    // =========================================================
    // Header
    // =========================================================

    private void createHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(12));
        header.getStyleClass().add("report-header");

        actionBar.setAlignment(Pos.CENTER_LEFT);
        actionBar.getStyleClass().add("report-action-bar");

        Button updateButton = new Button("Update report");
        Button uploadButton = new Button("Upload file");
        Button excelButton = new Button("Export Excel");
        Button pdfButton = new Button("Export PDF");

        updateButton.setOnAction(event -> updateReport());
        uploadButton.setOnAction(event -> uploadFile(uploadButton));

        excelButton.setOnAction(event ->
                exportReport(excelButton, ExportFormat.XLSX));

        pdfButton.setOnAction(event ->
                exportReport(pdfButton, ExportFormat.PDF));

        actionBar.getChildren().addAll(
                updateButton,
                uploadButton,
                excelButton,
                pdfButton
        );

        createLanguageButtons();

        versionBar.setAlignment(Pos.CENTER_LEFT);
        versionBar.getStyleClass().add("version-bar");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox navigationBar = new HBox(10);
        navigationBar.setAlignment(Pos.CENTER_LEFT);
        navigationBar.getStyleClass().add("report-navigation-bar");

        navigationBar.getChildren().addAll(
                versionBar,
                spacer,
                languageBar
        );

        header.getChildren().addAll(actionBar, navigationBar);
        setTop(header);
    }

    private void createLanguageButtons() {
        languageBar.setAlignment(Pos.CENTER_RIGHT);
        languageBar.getStyleClass().add("language-bar");

        englishButton = new Button("English");
        arabicButton = new Button("العربية");

        englishButton.getStyleClass().add("language-button");
        arabicButton.getStyleClass().add("language-button");

        englishButton.setOnAction(event -> changeLanguage(true));
        arabicButton.setOnAction(event -> changeLanguage(false));

        languageBar.getChildren().addAll(
                englishButton,
                arabicButton
        );

        updateLanguageButtons();
    }

    private void changeLanguage(boolean useEnglish) {
        if (english == useEnglish) {
            return;
        }

        english = useEnglish;
        updateLanguageButtons();
        showReport();
    }

    private void updateLanguageButtons() {
        englishButton.getStyleClass().remove("active");
        arabicButton.getStyleClass().remove("active");

        if (english) {
            englishButton.getStyleClass().add("active");
        } else {
            arabicButton.getStyleClass().add("active");
        }
    }

    // =========================================================
    // Controller actions
    // =========================================================

   private void updateReport() {
    if (summaryReport == null) {
        return;
    }

    reportsController.updateReport(
            summaryReport.getEncNumber()
    );

//     reportsController.refreshSummaryReports();

    loadReportVersions();
}
    private void uploadFile(Node source) {
        if (summaryReport == null) {
            return;
        }

        reportsController.uploadFile(
                source,
                summaryReport.getDirectory()
        );

        // Re-render the uploaded-files section.
        showReport();
    }

    private void exportReport(Node source, ExportFormat format) {
        if (currentReport == null) {
            return;
        }

        reportsController.exportingReport(
                source,
                currentReport.getFile(),
                format
        );
    }

    // =========================================================
    // Report selection
    // =========================================================

    public void setReport(SummaryCRReport summaryReport) {
    this.summaryReport = summaryReport;

    reportsController.setSelectedReport(summaryReport);

    loadReportVersions();
}

    private void loadReportVersions() {
        reports = summaryReport == null
                ? List.of()
                : reportsController.getReports(summaryReport);

        currentReport = reports.isEmpty()
                ? null
                : reports.get(reports.size() - 1);

        createVersionNavigation();
        showReport();
    }

    private void createVersionNavigation() {
        versionBar.getChildren().clear();

        for (int i = 0; i < reports.size(); i++) {
            CRReport report = reports.get(i);
            int version = i + 1;

            Button button = new Button("Version " + version);
            button.getStyleClass().add("version-button");

            if (report == currentReport) {
                button.getStyleClass().add("active");
            }

            button.setOnAction(event -> {
                currentReport = report;
                createVersionNavigation();
                showReport();
            });

            versionBar.getChildren().add(button);
        }
    }

    // =========================================================
    // Content
    // =========================================================

    private void createContent() {
        content.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);

        setCenter(scrollPane);
    }

    private void showReport() {
        content.getChildren().clear();

        fieldCount = 0;
        sectionCount = 0;

        if (currentReport == null) {
            Label emptyLabel = new Label("No report version available.");
            emptyLabel.getStyleClass().add("empty-report-message");
            content.getChildren().add(emptyLabel);
            return;
        }

        CRSection data = currentReport.getData();
        renderSection(data, content);

        if (summaryReport != null) {
            List<UploadedFile> uploadedFiles =
                    reportsController.getUploadedFiles(
                            summaryReport.getDirectory()
                    );

            UploadedFilesSection filesSection =
                    new UploadedFilesSection(uploadedFiles);

            content.getChildren().add(filesSection);
        }

        System.out.println("Fields: " + fieldCount);
        System.out.println("Sections: " + sectionCount);
    }

    // =========================================================
    // Render sections
    // =========================================================

    private void renderSection(CRSection section, VBox parent) {
        if (section == null) {
            return;
        }

        for (Map.Entry<String, CRField> entry :
                section.getFields().entrySet()) {

            fieldCount++;
            parent.getChildren().add(createField(entry.getValue()));
        }

        for (Map.Entry<String, CRSection> entry :
                section.getSections().entrySet()) {

            sectionCount++;

            parent.getChildren().add(
                    createCollapsibleSection(
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }
    }

    private VBox createCollapsibleSection(
            String name,
            CRSection section
    ) {
        VBox container = new VBox(5);
        container.getStyleClass().add("report-section");

        Button header = new Button("▶ " + formatSectionName(name));
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("report-section-button");

        VBox sectionContent = new VBox(10);
        sectionContent.setPadding(new Insets(10));
        sectionContent.setVisible(false);
        sectionContent.setManaged(false);

        boolean[] loaded = {false};

        header.setOnAction(event -> {
            boolean expanded = sectionContent.isVisible();

            sectionContent.setVisible(!expanded);
            sectionContent.setManaged(!expanded);

            header.setText(
                    (expanded ? "▶ " : "▼ ")
                            + formatSectionName(name)
            );

            if (!expanded && !loaded[0]) {
                renderSection(section, sectionContent);
                loaded[0] = true;
            }
        });

        container.getChildren().addAll(header, sectionContent);
        return container;
    }

    // =========================================================
    // Render fields
    // =========================================================

    private VBox createField(CRField field) {
        VBox box = new VBox(3);
        box.getStyleClass().add("report-field");

        String[] labels = field.getLabels();
        Object[] values = field.getValue();

        int languageIndex = english ? 0 : 1;

        String label = labels != null
                && labels.length > languageIndex
                && labels[languageIndex] != null
                ? labels[languageIndex]
                : "-";

        Object value = values != null
                && values.length > languageIndex
                ? values[languageIndex]
                : null;

        Label fieldLabel = new Label(label);
        fieldLabel.getStyleClass().add("report-field-label");

        ClickableLabel fieldValue = new ClickableLabel(
                cleanValue(value)
        );

        fieldValue.getStyleClass().add("report-field-value");
        fieldValue.setWrapText(true);
        fieldValue.setMaxWidth(Double.MAX_VALUE);

        if (!english) {
            fieldLabel.setAlignment(Pos.CENTER_RIGHT);
            fieldValue.setAlignment(Pos.CENTER_RIGHT);
            box.setAlignment(Pos.TOP_RIGHT);
        }

        box.getChildren().addAll(fieldLabel, fieldValue);
        return box;
    }

    // =========================================================
    // Formatting helpers
    // =========================================================

    private String formatSectionName(String name) {
        if (name == null || name.isBlank()) {
            return "-";
        }

        String result = name.replaceAll("([a-z])([A-Z])", "$1 $2");
        result = Character.toUpperCase(result.charAt(0))
                + result.substring(1);

        result = result.replaceAll("\\bCr\\b", "CR");
        result = result.replaceAll("\\bId\\b", "ID");

        return result;
    }

    private String cleanValue(Object value) {
        if (value == null) {
            return "-";
        }

        String text = String.valueOf(value);

        if (text.length() >= 2
                && text.startsWith("\"")
                && text.endsWith("\"")) {
            text = text.substring(1, text.length() - 1);
        }

        return text;
    }

    public SummaryCRReport getSummaryReport() {
        return summaryReport;
    }
}
