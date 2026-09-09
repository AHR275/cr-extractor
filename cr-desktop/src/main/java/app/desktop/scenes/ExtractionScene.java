package app.desktop.scenes;

import app.desktop.components.ExtractionComponent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class ExtractionScene extends BorderPane {

    public ExtractionScene() {

        Label title =
                new Label("CR Extractor");

        ExtractionComponent extraction =
                new ExtractionComponent();

        setTop(title);
        setCenter(extraction);
    }
}