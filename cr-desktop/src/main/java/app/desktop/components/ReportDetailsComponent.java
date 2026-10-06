package app.desktop.components;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.core.models.CRField;
import app.core.models.CRReport;
import app.core.models.CRSection;
import app.core.models.SummaryCRReport;
import app.core.models.UploadedFile;
import app.core.services.CRRepoertService;
import app.core.services.UploadingFilesService;
import app.desktop.controllers.ReportsController;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class ReportDetailsComponent extends BorderPane {

    private SummaryCRReport summaryReport;

    private List<CRReport> reports =
            new ArrayList<>();

    private CRReport currentReport;

    private final CRRepoertService crRepoertService =
            new CRRepoertService();

    private final UploadingFilesService uploadingFilesService =
            new UploadingFilesService();

    private final VBox content;

    private final HBox versionBar;

    private final HBox languageBar;

    private int fieldCount = 0;

    private int sectionCount = 0;

    private final ReportsController reportsController =
            new ReportsController();

    private File selectedFile;

    /*
     * true  = English
     * false = Arabic
     */
    private boolean english = true;


    public ReportDetailsComponent() {

        getStyleClass().add(
                "report-details"
        );


        // =====================================================
        // Top navigation
        // =====================================================

        VBox header =
                new VBox();


        Button updateButton =
                new Button("update report data");

        updateButton.setOnAction(event -> {

            if (summaryReport != null) {

                reportsController.updateReport(
                        summaryReport.getEncNumber()
                );
            }

        });


        Button uploadFileButton =
                new Button("upload a file");

        uploadFileButton.setOnAction(event -> {

            if (summaryReport != null) {

                reportsController.uploadFile(
                        header,
                        summaryReport.getDirectory()
                );

                /*
                 * Refresh the uploaded files section after
                 * uploading a file.
                 */
                showReport();
            }

        });


        HBox topBar =
                new HBox(20);

        topBar.setPadding(
                new Insets(10)
        );

        topBar.setAlignment(
                Pos.CENTER_LEFT
        );

        topBar.getStyleClass().add(
                "report-top-bar"
        );


        // =====================================================
        // Version navigation
        // =====================================================

        versionBar =
                new HBox(10);

        versionBar.setAlignment(
                Pos.CENTER_LEFT
        );

        versionBar.getStyleClass().add(
                "version-bar"
        );


        // =====================================================
        // Language navigation
        // =====================================================

        languageBar =
                new HBox(5);

        languageBar.setAlignment(
                Pos.CENTER_RIGHT
        );

        languageBar.getStyleClass().add(
                "language-bar"
        );


        Button englishButton =
                new Button("English");

        Button arabicButton =
                new Button("العربية");


        englishButton.getStyleClass().add(
                "language-button"
        );

        arabicButton.getStyleClass().add(
                "language-button"
        );


        /*
         * Initially English is selected.
         */
        englishButton.getStyleClass().add(
                "active"
        );


        englishButton.setOnAction(event -> {

            if (!english) {

                english = true;

                updateLanguageButtons();

                showReport();
            }

        });


        arabicButton.setOnAction(event -> {

            if (english) {

                english = false;

                updateLanguageButtons();

                showReport();
            }

        });


        languageBar
                .getChildren()
                .addAll(
                        englishButton,
                        arabicButton
                );


        /*
         * Put version navigation on the left
         * and language navigation on the right.
         */
        HBox.setMargin(
                languageBar,
                new Insets(0, 0, 0, 10)
        );


        topBar
                .getChildren()
                .addAll(
                        versionBar,
                        languageBar
                );


        header.getChildren()
                .addAll(
                        updateButton,
                        uploadFileButton,
                        topBar
                );

        setTop(header);


        // =====================================================
        // Report content
        // =====================================================

        content =
                new VBox(20);

        content.setPadding(
                new Insets(20)
        );


        ScrollPane scrollPane =
                new ScrollPane(content);

        scrollPane.setFitToWidth(true);

        setCenter(scrollPane);
    }


    // =========================================================
    // Set report
    // =========================================================

    public void setReport(
            SummaryCRReport summaryReport
    ) {

        this.summaryReport =
                summaryReport;


        this.reports =
                crRepoertService.getReports(
                        summaryReport
                );


        if (reports.isEmpty()) {

            currentReport = null;

        } else {

            // Last one = newest version
            currentReport =
                    reports.get(
                            reports.size() - 1
                    );
        }


        createVersionNavigation();

        showReport();
    }


    // =========================================================
    // Update language buttons
    // =========================================================

    private void updateLanguageButtons() {

        /*
         * Get the buttons from the language bar.
         */

        Button englishButton =
                (Button) languageBar
                        .getChildren()
                        .get(0);

        Button arabicButton =
                (Button) languageBar
                        .getChildren()
                        .get(1);


        englishButton
                .getStyleClass()
                .remove("active");

        arabicButton
                .getStyleClass()
                .remove("active");


        if (english) {

            englishButton
                    .getStyleClass()
                    .add("active");

        } else {

            arabicButton
                    .getStyleClass()
                    .add("active");
        }
    }


    // =========================================================
    // Version navigation
    // =========================================================

    private void createVersionNavigation() {

        versionBar
                .getChildren()
                .clear();


        for (int i = 0;
             i < reports.size();
             i++) {

            CRReport report =
                    reports.get(i);


            int version =
                    i + 1;


            Button button =
                    new Button(
                            "Version " + version
                    );


            button.getStyleClass().add(
                    "version-button"
            );


            if (report == currentReport) {

                button.getStyleClass().add(
                        "active"
                );
            }


            button.setOnAction(event -> {

                currentReport =
                        report;

                createVersionNavigation();

                showReport();
            });


            versionBar
                    .getChildren()
                    .add(button);
        }
    }


    // =========================================================
    // Show report
    // =========================================================

    private void showReport() {

        content
                .getChildren()
                .clear();


        if (currentReport == null) {
            return;
        }


        CRSection data =
                currentReport.getData();


        fieldCount = 0;

        sectionCount = 0;


        /*
         * =====================================================
         * CR report
         * =====================================================
         */

        renderSection(
                data,
                content
        );


        /*
         * =====================================================
         * Uploaded files
         * =====================================================
         *
         * Uploaded files are NOT CR fields or CR sections.
         * They are displayed using their own TableView
         * component.
         */

        if (summaryReport != null) {

            List<UploadedFile> uploadedFiles =
                    uploadingFilesService.getUploadedFiles(
                            summaryReport.getDirectory()
                    );


            UploadedFilesSection uploadedFilesSection =
                    new UploadedFilesSection(
                            uploadedFiles
                    );


            content.getChildren().add(
                    uploadedFilesSection
            );
        }


        System.out.println(
                "fields : " + fieldCount
        );

        System.out.println(
                "sections : " + sectionCount
        );
    }


    // =========================================================
    // Render section
    // =========================================================

    private void renderSection(
            CRSection section,
            VBox parent
    ) {


        // =====================================================
        // Fields directly inside this section
        // =====================================================

        for (Map.Entry<String, CRField> entry :
                section.getFields().entrySet()) {


            fieldCount++;


            CRField field =
                    entry.getValue();


            VBox fieldBox =
                    createField(field);


            parent
                    .getChildren()
                    .add(fieldBox);
        }


        // =====================================================
        // Child sections
        // =====================================================

        for (Map.Entry<String, CRSection> entry :
                section.getSections().entrySet()) {


            sectionCount++;


            String sectionName =
                    entry.getKey();


            CRSection child =
                    entry.getValue();


            VBox collapsibleSection =
                    createCollapsibleSection(
                            sectionName,
                            child
                    );


            parent
                    .getChildren()
                    .add(
                            collapsibleSection
                    );
        }
    }


    // =========================================================
    // Create collapsible section
    // =========================================================

    private VBox createCollapsibleSection(
            String name,
            CRSection section
    ) {

        VBox container =
                new VBox(5);


        container.getStyleClass().add(
                "report-section"
        );


        // =====================================================
        // Section header
        // =====================================================

        Button header =
                new Button();


        header.setMaxWidth(
                Double.MAX_VALUE
        );


        header.setText(
                "▶ " +
                formatSectionName(name)
        );


        header.getStyleClass().add(
                "report-section-button"
        );


        // =====================================================
        // Section content
        // =====================================================

        VBox sectionContent =
                new VBox(10);


        sectionContent.setPadding(
                new Insets(10)
        );


        sectionContent.setVisible(
                false
        );

        sectionContent.setManaged(
                false
        );


        /*
         * Prevent rendering the section more than once
         * while this version remains displayed.
         */

        final boolean[] loaded =
                { false };


        // =====================================================
        // Click section
        // =====================================================

        header.setOnAction(event -> {


            boolean expanded =
                    sectionContent.isVisible();


            // =================================================
            // Collapse
            // =================================================

            if (expanded) {

                sectionContent.setVisible(
                        false
                );

                sectionContent.setManaged(
                        false
                );


                header.setText(
                        "▶ " +
                        formatSectionName(name)
                );


                return;
            }


            // =================================================
            // Expand
            // =================================================

            sectionContent.setVisible(
                    true
            );

            sectionContent.setManaged(
                    true
            );


            header.setText(
                    "▼ " +
                    formatSectionName(name)
            );


            // =================================================
            // Lazy rendering
            // =================================================

            if (!loaded[0]) {

                renderSection(
                        section,
                        sectionContent
                );

                loaded[0] = true;
            }
        });


        // =====================================================
        // Add header + content
        // =====================================================

        container
                .getChildren()
                .addAll(
                        header,
                        sectionContent
                );


        return container;
    }


    // =========================================================
    // Create field
    // =========================================================

    private VBox createField(
            CRField field
    ) {

        VBox box =
                new VBox(3);


        String[] labels =
                field.getLabels();

        Object[] values =
                field.getValue();


        box.getStyleClass().add(
                "report-field"
        );


        // =====================================================
        // Selected language
        // =====================================================

        int languageIndex =
                english ? 0 : 1;


        String label =
                labels[languageIndex];


        Object value =
                values[languageIndex];


        // =====================================================
        // Label
        // =====================================================

        Label fieldLabel =
                new Label(
                        label
                );


        fieldLabel.getStyleClass().add(
                "report-field-label"
        );


        // =====================================================
        // Value
        // =====================================================

        ClickableLabel fieldValue =
                new ClickableLabel(
                        value == null
                                ? "-"
                                : cleanValue(value)
                );


        fieldValue.getStyleClass().add(
                "report-field-value"
        );


        fieldValue.setWrapText(
                true
        );


        // =====================================================
        // Arabic alignment
        // =====================================================

        if (!english) {

            fieldLabel.setAlignment(
                    Pos.CENTER_RIGHT
            );

            fieldValue.setAlignment(
                    Pos.CENTER_RIGHT
            );

            box.setAlignment(
                    Pos.TOP_RIGHT
            );
        }


        // =====================================================
        // Add field
        // =====================================================

        box
                .getChildren()
                .addAll(
                        fieldLabel,
                        fieldValue
                );


        return box;
    }


    // =========================================================
    // Format section name
    // =========================================================

    private String formatSectionName(
            String name
    ) {

        if (name == null ||
                name.isEmpty()) {

            return "-";
        }


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


    // =========================================================
    // Clean value
    // =========================================================

    private String cleanValue(
            Object value
    ) {

        if (value == null) {

            return "-";
        }


        String text =
                String.valueOf(value);


        /*
         * Remove surrounding quotes.
         */

        if (text.length() >= 2 &&
                text.startsWith("\"") &&
                text.endsWith("\"")) {

            text =
                    text.substring(
                            1,
                            text.length() - 1
                    );
        }


        return text;
    }


    // =========================================================
    // Getter
    // =========================================================

    public SummaryCRReport getSummaryReport() {

        return summaryReport;
    }
}