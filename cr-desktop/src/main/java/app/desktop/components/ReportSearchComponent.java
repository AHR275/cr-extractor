package app.desktop.components;

import java.util.List;
import java.util.function.Consumer;

import app.core.models.ReportSearchCriteria;
import app.desktop.controllers.ReportsController;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ReportSearchComponent extends VBox {

    private final TextField queryField =
            new TextField();

    private final TextField crNumberField =
            new TextField();

    private final TextField companyNameField =
            new TextField();

    private final ComboBox<String> statusComboBox =
            new ComboBox<>();

    private final Button searchButton =
            new Button("Search");

    private final Button clearButton =
            new Button("Clear");

    private final Button refreshButton =
            new Button("Refresh");

    private final Consumer<ReportSearchCriteria> onSearch;
    private final Runnable onClear;
    private final Runnable onRefresh;

    private final ReportsController reportsController; 

    public ReportSearchComponent(
            Consumer<ReportSearchCriteria> onSearch,
            Runnable onClear,
            Runnable onRefresh,
            ReportsController rc 
        ) {
        this.reportsController = rc ; 
        this.onSearch = onSearch;
        this.onClear = onClear;
        this.onRefresh = onRefresh;

        initializeFields();
        configureLayout();
        configureActions();
    }

    private void initializeFields() {
        queryField.setPromptText(
                "Company name, CR number, or link..."
        );

        crNumberField.setPromptText("Enter CR number");
        companyNameField.setPromptText("Enter company name");

        statusComboBox.setPromptText("All statuses");
        statusComboBox.setMaxWidth(Double.MAX_VALUE);

        statusComboBox.getItems().add("All statuses");
        statusComboBox.setValue("All statuses");

        searchButton.getStyleClass().add("primary-button");
        clearButton.getStyleClass().add("secondary-button");
        refreshButton.getStyleClass().add("secondary-button");
    }

    private void configureLayout() {
        Label globalSearchLabel = new Label("Global search");
        Label crNumberLabel = new Label("CR number");
        Label companyNameLabel = new Label("Company name");
        Label statusLabel = new Label("Status");

        GridPane filters = new GridPane();
        filters.setHgap(12);
        filters.setVgap(8);

        filters.add(globalSearchLabel, 0, 0);
        filters.add(crNumberLabel, 1, 0);
        filters.add(companyNameLabel, 2, 0);
        filters.add(statusLabel, 3, 0);

        filters.add(queryField, 0, 1);
        filters.add(crNumberField, 1, 1);
        filters.add(companyNameField, 2, 1);
        filters.add(statusComboBox, 3, 1);

        for (int column = 0; column < 4; column++) {
            GridPane.setHgrow(
                    column == 0 ? queryField
                            : column == 1 ? crNumberField
                            : column == 2 ? companyNameField
                            : statusComboBox,
                    Priority.ALWAYS
            );
        }

        HBox actions = new HBox(
                10,
                searchButton,
                clearButton,
                refreshButton
        );

        actions.getStyleClass().add("search-actions");

        setSpacing(12);
        setPadding(new Insets(16));
        getStyleClass().add("report-search-component");

        getChildren().addAll(filters, actions);
    }

    private void configureActions() {
        searchButton.setOnAction(event -> submitSearch());

        queryField.setOnAction(event -> submitSearch());
        crNumberField.setOnAction(event -> submitSearch());
        companyNameField.setOnAction(event -> submitSearch());

        statusComboBox.setOnAction(event -> submitSearch());

        clearButton.setOnAction(event -> onClear.run());
        refreshButton.setOnAction(event -> onRefresh.run());

        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                submitSearch();
            }
        });
    }

    private void submitSearch() {
        if (onSearch != null) {
            onSearch.accept(getCriteria());
        }
    }

    public ReportSearchCriteria getCriteria() {
        String status = statusComboBox.getValue();

        if (status == null || status.equals("All statuses")) {
            status = "";
        }

        return new ReportSearchCriteria(
                safeText(queryField.getText()),
                safeText(crNumberField.getText()),
                safeText(companyNameField.getText()),
                safeText(status)
        );
    }

    public void resetFields() {
        queryField.clear();
        crNumberField.clear();
        companyNameField.clear();

        statusComboBox.setValue("All statuses");
    }

    public void setStatuses(List<String> statuses) {
        String previous = statusComboBox.getValue();

        statusComboBox.getItems().setAll("All statuses");
        statusComboBox.getItems().addAll(statuses);

        if (previous != null
                && statusComboBox.getItems().contains(previous)) {
            statusComboBox.setValue(previous);
        } else {
            statusComboBox.setValue("All statuses");
        }
    }

    private String safeText(String value) {
        return value == null ? "" : value.trim();
    }
}