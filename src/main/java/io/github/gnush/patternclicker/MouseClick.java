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

import javafx.geometry.Point2D;
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

    public Point2D asPoint() {
        return new Point2D(x, y);
    }
}