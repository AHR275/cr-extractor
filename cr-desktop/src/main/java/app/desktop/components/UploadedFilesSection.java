
package app.desktop.components;

import app.core.models.UploadedFile;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.List;

public class UploadedFilesSection extends VBox {

    private final TableView<UploadedFile> table = new TableView<>();

    public UploadedFilesSection(List<UploadedFile> uploadedFiles) {

        setSpacing(10);
        setPadding(new Insets(10));

        createTable();

        table.setItems(
            FXCollections.observableArrayList(uploadedFiles)
        );

        getChildren().add(table);
    }

    private void createTable() {

        // =========================
        // Name column
        // =========================

        TableColumn<UploadedFile, String> nameColumn =
                new TableColumn<>("Name");

        nameColumn.setCellValueFactory(
            data -> new SimpleStringProperty(
                data.getValue().getName()
            )
        );


        // =========================
        // Type column
        // =========================

        TableColumn<UploadedFile, String> typeColumn =
                new TableColumn<>("Type");

        typeColumn.setCellValueFactory(
            data -> new SimpleStringProperty(
                getFileExtension(
                    data.getValue().getName()
                )
            )
        );


        // =========================
        // Size column
        // =========================

        TableColumn<UploadedFile, String> sizeColumn =
                new TableColumn<>("Size");

        sizeColumn.setCellValueFactory(
            data -> new SimpleStringProperty(
                formatSize(
                    data.getValue().getSize()
                )
            )
        );


        // =========================
        // Add columns
        // =========================

        table.getColumns().addAll(
            nameColumn,
            typeColumn,
            sizeColumn
        );


        // =========================
        // Table configuration
        // =========================

        table.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY
        );

        table.setPlaceholder(
            new Label("No uploaded files")
        );


        // =========================
        // Open file on double-click
        // =========================

        table.setOnMouseClicked(event -> {

            if (event.getClickCount() == 2) {

                UploadedFile file =
                        table.getSelectionModel()
                             .getSelectedItem();

                if (file == null) {
                    return;
                }

                try {

                    java.awt.Desktop
                            .getDesktop()
                            .open(file.getPath().toFile());

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        });
    }


    // =========================
    // Get file extension
    // =========================

    private String getFileExtension(String name) {

        int index = name.lastIndexOf('.');

        if (index == -1) {
            return "";
        }

        return name
                .substring(index + 1)
                .toUpperCase();
    }


    // =========================
    // Format file size
    // =========================

    private String formatSize(long bytes) {

        if (bytes < 1024) {

            return bytes + " B";
        }

        if (bytes < 1024 * 1024) {

            return String.format(
                "%.1f KB",
                bytes / 1024.0
            );
        }

        if (bytes < 1024 * 1024 * 1024) {

            return String.format(
                "%.1f MB",
                bytes / (1024.0 * 1024.0)
            );
        }

        return String.format(
            "%.1f GB",
            bytes / (1024.0 * 1024.0 * 1024.0)
        );
    }
}
