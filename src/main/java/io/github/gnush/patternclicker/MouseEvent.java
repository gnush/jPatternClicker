package io.github.gnush.patternclicker;

import javafx.scene.input.MouseButton;

public record MouseEvent(
        MouseButton button,
        int x,
        int y,
        long delayMillis
) {
    @Override
    public String toString() {
        return button.toString() + " (" + x + "," +  y + ") " + delayMillis + "ms";
    }
}