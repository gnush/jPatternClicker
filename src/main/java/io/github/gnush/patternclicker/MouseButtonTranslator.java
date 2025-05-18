package io.github.gnush.patternclicker;

import javafx.scene.input.MouseButton;

import java.util.Optional;

public final class MouseButtonTranslator {
    public static Optional<MouseButton> fromNative(int button) {
        return switch (button) {
            case 1 -> Optional.of(MouseButton.PRIMARY);
            case 2 -> Optional.of(MouseButton.SECONDARY);
            case 3 -> Optional.of(MouseButton.MIDDLE);
            case 4 -> Optional.of(MouseButton.BACK);
            case 5 -> Optional.of(MouseButton.FORWARD);
            default -> Optional.empty();
        };
    }
}
