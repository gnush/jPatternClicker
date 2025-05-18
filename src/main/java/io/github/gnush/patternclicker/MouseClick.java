package io.github.gnush.patternclicker;

import javafx.scene.input.MouseButton;

public record MouseClick(
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