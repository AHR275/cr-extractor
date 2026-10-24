package app.desktop.scenes;

import java.util.List;
import java.util.function.Consumer;

import app.core.models.ReportSearchCriteria;
import app.core.models.SummaryCRReport;
import app.desktop.components.ReportResultsComponent;
import app.desktop.components.ReportSearchComponent;
import app.desktop.controllers.ReportsController;
import app.desktop.ctx.ControllersContexts;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class DashboardScene extends BorderPane {

    private final ReportsController reportsController;
    // private final Consumer<SummaryCRReport> onOpenReport;
    
    private final ReportSearchComponent searchComponent;
    private final ReportResultsComponent resultsComponent;
    // resultsComponent = new ReportResultsComponent();
    // resultsComponent.setOnSelectedReport(this::openReport);

    private final Label totalReportsValue = new Label("0");
    private final Label activeReportsValue = new Label("0");
    private final Label otherReportsValue = new Label("0");

    public DashboardScene( ControllersContexts CtrlCtx ) {

        this.reportsController = CtrlCtx.getReportsController();
        // this.onOpenReport = onOpenReport;

        setPadding(new Insets(24));
        getStyleClass().add("dashboard-scene");

        searchComponent = new ReportSearchComponent(
                this::searchReports,
                this::clearSearch,
                this::refreshDashboard, 
                reportsController
                
        );

        resultsComponent = new ReportResultsComponent( CtrlCtx);

        setTop(createHeader());
        setCenter(createMainContent());

        refreshDashboard();
    }

    private VBox createHeader() {
        Label title = new Label("Dashboard");
        title.getStyleClass().add("dashboard-title");

        Label subtitle = new Label(
                "Search and manage your commercial registration reports."
        );
        subtitle.getStyleClass().add("dashboard-subtitle");

        VBox header = new VBox(6, title, subtitle);
        header.setPadding(new Insets(0, 0, 20, 0));

        return header;
    }

    private VBox createMainContent() {
        Label statisticsTitle = new Label("Overview");
        statisticsTitle.getStyleClass().add("section-title");

        HBox statistics = new HBox(
                16,
                createStatCard("Total reports", totalReportsValue),
                createStatCard("Active reports", activeReportsValue),
                createStatCard("Other statuses", otherReportsValue)
        );

        statistics.setAlignment(Pos.CENTER_LEFT);
        statistics.getStyleClass().add("statistics-row");

        Label searchTitle = new Label("Search reports");
        searchTitle.getStyleClass().add("section-title");

        VBox searchSection = new VBox(
                12,
                searchTitle,
                searchComponent
        );
        searchSection.getStyleClass().add("dashboard-panel");

        VBox resultsSection = new VBox(
                12,
                resultsComponent
        );
        resultsSection.getStyleClass().add("dashboard-panel");

        VBox content = new VBox(
                18,
                statisticsTitle,
                statistics,
                searchSection,
                resultsSection
        );

        content.setFillWidth(true);
        VBox.setVgrow(resultsSection, Priority.ALWAYS);

        return content;
    }

    private VBox createStatCard(String title, Label value) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stat-card-title");

        value.getStyleClass().add("stat-card-value");

        VBox card = new VBox(8, titleLabel, value);
        card.setPadding(new Insets(18));
        card.setMinWidth(150);
        card.setMaxWidth(Double.MAX_VALUE);
        card.getStyleClass().add("stat-card");

        HBox.setHgrow(card, Priority.ALWAYS);

        return card;
    }

    private void searchReports(ReportSearchCriteria criteria) {
        try {
            List<SummaryCRReport> results =
                    reportsController.searchReports(criteria);

            resultsComponent.setReports(results);

        } catch (RuntimeException exception) {
            resultsComponent.showError(
                    "Unable to search reports: " + exception.getMessage()
            );
        }
    }

    private void clearSearch() {
        searchComponent.resetFields();
        refreshDashboard();
    }

    public void refreshDashboard() {
        try {
            // reportsController.refreshSummaryReports();

            List<SummaryCRReport> reports =
                    reportsController.getSummaryReports();

            updateStatistics(reports);

            searchComponent.setStatuses(
                    reports.stream()
                            .map(SummaryCRReport::getStatus)
                            .filter(status ->
                                    status != null && !status.isBlank())
                            .distinct()
                            .sorted(String.CASE_INSENSITIVE_ORDER)
                            .toList()
            );

            ReportSearchCriteria criteria =
                    searchComponent.getCriteria();

            resultsComponent.setReports(
                    reportsController.searchReports(criteria)
            );

        } catch (RuntimeException exception) {
            resultsComponent.showError(
                    "Unable to load reports: " + exception.getMessage()
            );
        }
    }

    private void updateStatistics(List<SummaryCRReport> reports) {
        long total = reports.size();

        long active = reports.stream()
                .map(SummaryCRReport::getStatus)
                .filter(status -> status != null)
                .filter(status -> status.trim().equalsIgnoreCase("Active"))
                .count();

        totalReportsValue.setText(String.valueOf(total));
        activeReportsValue.setText(String.valueOf(active));
        otherReportsValue.setText(String.valueOf(total - active));
    }

    // public void openReport(SummaryCRReport report) {
    //     if (onOpenReport != null && report != null) {
    //         onOpenReport.accept(report);
    //     }
    // }
}