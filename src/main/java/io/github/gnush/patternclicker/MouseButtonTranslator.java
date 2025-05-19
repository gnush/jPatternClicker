/*
 * jPatternClicker: Record and replay mouse click patterns.
 * Copyright (C) 2025 gnush
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
