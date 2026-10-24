package app.desktop.components;

import java.util.List;

import app.core.models.SummaryCRReport;
import app.desktop.controllers.NavigationController;
import app.desktop.controllers.ReportsController;
import app.desktop.ctx.ControllersContexts;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class ReportResultsComponent extends BorderPane {

    private final TableView<SummaryCRReport> table =
            new TableView<>();

    private final ObservableList<SummaryCRReport> reports =
            FXCollections.observableArrayList();

    private final Label resultsLabel =
            new Label("Reports");

    private final Label messageLabel =
            new Label();

//     private final Consumer<SummaryCRReport> onOpenReport;
    private final ReportsController reportsController; 
    private final NavigationController navigationController; 

    public ReportResultsComponent(
            
            ControllersContexts CrtlCtx
        ) {
        this.reportsController = CrtlCtx.getReportsController() ; 
        this.navigationController = CrtlCtx.getNavigationController() ; 
        // this.onOpenReport = onOpenReport;

        initializeTable();
        configureLayout();
        configureActions();
    }

    private void initializeTable() {
        table.setItems(reports);
        table.setPlaceholder(new Label("No reports found."));
        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );

        TableColumn<SummaryCRReport, String> companyColumn =
                new TableColumn<>("Company name");

        companyColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        safe(cell.getValue().getCompanyName())
                )
        );
        companyColumn.setPrefWidth(320);

        TableColumn<SummaryCRReport, String> crNumberColumn =
                new TableColumn<>("CR number");

        crNumberColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        safe(cell.getValue().getCrNumber())
                )
        );
        crNumberColumn.setPrefWidth(150);

        TableColumn<SummaryCRReport, String> statusColumn =
                new TableColumn<>("Status");

        statusColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        safe(cell.getValue().getStatus())
                )
        );
        statusColumn.setPrefWidth(130);

        TableColumn<SummaryCRReport, String> linkColumn =
                new TableColumn<>("Reference");

        linkColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        safe(cell.getValue().getEncNumber())
                )
        );
        linkColumn.setPrefWidth(180);

        TableColumn<SummaryCRReport, Void> actionColumn =
                new TableColumn<>("Actions");

        actionColumn.setSortable(false);
        actionColumn.setCellFactory(column -> new TableCell<>() {

            private final Button openButton = new Button("Open");

            {
                openButton.getStyleClass().add("primary-button");

                openButton.setOnAction(event -> {
                        SummaryCRReport report = getTableRow().getItem();

                        if (report != null) {
                                reportsController.setSelectedReport(report);
                                navigationController.showReports();
                                // navigationController.getScenes().get("reports").showReport();
                        }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getTableRow() == null
                        || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(openButton);
                }
            }
        });

        actionColumn.setPrefWidth(100);

        table.getColumns().setAll(
                companyColumn,
                crNumberColumn,
                statusColumn,
                linkColumn,
                actionColumn
        );
    }

    private void configureLayout() {
        resultsLabel.getStyleClass().add("section-title");
        messageLabel.getStyleClass().add("secondary-text");

        HBox header = new HBox(12, resultsLabel, messageLabel);
        header.setPadding(new Insets(0, 0, 12, 0));

        setTop(header);
        setCenter(table);

        setPadding(new Insets(16));
        getStyleClass().add("report-results-component");
    }

    private void configureActions() {
    table.setOnMouseClicked(event -> {
        if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY
                && event.getClickCount() == 2) {

            SummaryCRReport selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected != null) {
                reportsController.setSelectedReport(selected);
            }
        }
    });
}
    public void setReports(List<SummaryCRReport> newReports) {
        reports.setAll(newReports == null ? List.of() : newReports);

        resultsLabel.setText(
                "Reports (" + reports.size() + ")"
        );

        messageLabel.setText("");
    }

    public void showError(String message) {
        reports.clear();
        resultsLabel.setText("Reports");
        messageLabel.setText(message);
    }

    public SummaryCRReport getSelectedReport() {
        return table.getSelectionModel().getSelectedItem();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}