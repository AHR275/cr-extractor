package app.desktop.components;

import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.util.Duration; 


public class ClickableLabel extends Label {

    private final Clipboard clipboard = Clipboard.getSystemClipboard();

    public ClickableLabel(String text) {
        super(text);

        setOnMouseClicked(e -> {
            ClipboardContent content = new ClipboardContent();
            content.putString(getText());

            clipboard.setContent(content);

             Tooltip tooltip = new Tooltip("Copied!");

            Tooltip.install(this, tooltip);

            tooltip.show(
                    this,
                    e.getScreenX(),
                    e.getScreenY()
            );

              // Hide after 1 second
            PauseTransition pause =
                    new PauseTransition(
                            Duration.seconds(1)
                    );

            pause.setOnFinished(event ->
                    tooltip.hide()
            );

            pause.play();

        });
    }
}