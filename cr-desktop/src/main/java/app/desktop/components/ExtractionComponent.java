package app.desktop.components;

import java.io.File;

import app.core.services.ExtractionService;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class ExtractionComponent extends VBox {

    private final ExtractionService service;

    private final Label fileLabel;
    private final TextArea resultArea;

    private File selectedFile;

    public ExtractionComponent() {

        service = new ExtractionService();

        // CSS class for the whole component
        getStyleClass().add("extraction-component");

        Button chooseButton =
                new Button("Choose a Document");

        Button extractButton =
                new Button("Extract");

        fileLabel =
                new Label("No file selected");

        resultArea =
                new TextArea();

        // CSS classes
        chooseButton.getStyleClass().add("secondary-button");
        extractButton.getStyleClass().add("primary-button");

        fileLabel.getStyleClass().add("file-label");

        resultArea.getStyleClass().add("result-area");

        resultArea.setEditable(false);

        chooseButton.setOnAction(
                e -> chooseFile()
        );

        extractButton.setOnAction(
                e -> extract()
        );

        getChildren().addAll(
                chooseButton,
                fileLabel,
                extractButton,
                resultArea
        );
    }

    private void chooseFile() {

        FileChooser chooser =
                new FileChooser();

        chooser.getExtensionFilters()
               .add(
                    new FileChooser.ExtensionFilter(
                        "Documents",
                        "*.pdf",
                        "*.png",
                        "*.jpg",
                        "*.jpeg"
                    )
               );

        selectedFile =
                chooser.showOpenDialog(
                        getScene().getWindow()
                );

        if (selectedFile != null) {

            fileLabel.setText(
                    selectedFile.getAbsolutePath()
            );
        }
    }

    private void extract() {

        if (selectedFile == null) {

            resultArea.setText(
                    "Please select a file first"
            );

            return;
        }

        try {

            String result =
                    service.extract(
                            selectedFile.toPath()
                    );

            resultArea.setText(result);

        } catch (Exception e) {

            resultArea.setText(
                    e.getMessage()
            );
        }
    }
}