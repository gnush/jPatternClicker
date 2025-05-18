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
}
