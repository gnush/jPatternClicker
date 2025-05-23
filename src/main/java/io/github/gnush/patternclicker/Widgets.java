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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.util.converter.NumberStringConverter;

public final class Widgets {
    public static GridPane createTwoColumGridPane() {
        GridPane grid = new GridPane();

        grid.getColumnConstraints().addAll(createJustifiedConstraint(HPos.RIGHT), createJustifiedConstraint(HPos.LEFT));
        grid.setHgap(6);
        grid.setVgap(4);
        grid.setPadding(new Insets(4));
        return grid;
    }

    private static ColumnConstraints createJustifiedConstraint(HPos alignment) {
        ColumnConstraints result = new ColumnConstraints();
        result.setHalignment(alignment);
        return result;
    }

    public static TextField createInputField(StringProperty binding) {
        TextField result = new TextField();
        result.setMaxWidth(100);
        result.textProperty().bindBidirectional(binding);
        return result;
    }

    public static Text createPromptText(String prompt) {
        Text result = new Text(prompt);
        result.maxWidth(100);
        result.getStyleClass().add("label-text");
        return result;
    }

    public static Text createPromptText(StringProperty binding) {
        Text result = new Text();
        result.getStyleClass().add("label-text");
        result.textProperty().bind(binding);
        return result;
    }

    public static Text createPromptText(DoubleProperty binding) {
        Text result = new Text();
        result.getStyleClass().add("label-text");
        result.textProperty().bindBidirectional(binding, new NumberStringConverter());
        return result;
    }

    public static Text createStopSign() {
        Text text = new Text("⏹");
        text.getStyleClass().add("stop-sign");
        return text;
    }

    public static Text createPlaySign() {
        Text text = new Text("⏵");
        text.getStyleClass().add("play-sign");
        return text;
    }

    public static Text createRecordSign() {
        Text text = new Text("⏺");
        text.getStyleClass().add("record-sign");
        return text;
    }
}
