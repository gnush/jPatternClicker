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

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.util.Builder;

import static io.github.gnush.patternclicker.Widgets.*;

public class ScreenBuilder implements Builder<Region> {
    private final Runnable recordAction;
    private final Runnable replayAction;
    private final Runnable clearAction;

    private final ViewModel viewModel;

    public ScreenBuilder(ViewModel viewModel, Runnable recordAction, Runnable replayAction, Runnable clearAction) {
        this.recordAction = recordAction;
        this.replayAction = replayAction;
        this.clearAction = clearAction;
        this.viewModel = viewModel;
    }

    @Override
    public Region build() {
        BorderPane result = new BorderPane();

        Node headingBox = createTopButtonRow();
        BorderPane.setMargin(headingBox, new Insets(4));
        result.setTop(headingBox);
        result.setCenter(createCenterContent());

        return result;
    }

    private Node createTopButtonRow() {
        ButtonBar result = new ButtonBar();
        result.setButtonMinWidth(100);
        result.setPadding(new Insets(2));

        //Button recordButton = new Button("", createRecordSign()); // would need to dynamically recreate button to remove and re-add to button bar
        Button recordButton = new Button();
        recordButton.textProperty().bind(viewModel.recordButtonProperty());
        recordButton.setOnAction(event -> {
            viewModel.stopReplay();
            viewModel.toggleRecording();
            recordAction.run();
        });

        Button replayButton = new Button();
        replayButton.textProperty().bind(viewModel.replayButtonProperty());
        replayButton.setOnAction(event -> {
            viewModel.stopRecording();
            viewModel.toggleReplay();
            replayAction.run();
        });

        Button clearButton = new Button("Clear All");
        clearButton.setOnAction(event -> clearAction.run());

        ButtonBar.setButtonData(recordButton, ButtonBar.ButtonData.LEFT);
        ButtonBar.setButtonData(clearButton, ButtonBar.ButtonData.LEFT);
        ButtonBar.setButtonData(replayButton, ButtonBar.ButtonData.RIGHT);

        result.getButtons().addAll(recordButton, clearButton, replayButton);

        return result;
    }

    private Node createCenterContent() {
        HBox box = new HBox(10);
        box.setPadding(new Insets(2, 10, 4, 2));

        box.getChildren().addAll(createMouseStatisticsView(), createMouseEventList());

        return box;
    }

    private Node createMouseStatisticsView() {
        GridPane grid = createTwoColumGridPane();
        grid.setMinWidth(250);

        grid.add(createPromptText("Loop delay (ms):"), 0, 0);
        grid.add(createInputField(viewModel.repetitionDelay), 1, 0);

        grid.add(createPromptText("Mouse X:"), 0, 1);
        grid.add(createPromptText(viewModel.mouseX), 1, 1);
        grid.add(createPromptText("Mouse Y:"), 0, 2);
        grid.add(createPromptText(viewModel.mouseY), 1, 2);

        grid.add(createPromptText("Click X:"), 0, 3);
        grid.add(createPromptText(viewModel.clickX), 1, 3);
        grid.add(createPromptText("Click Y:"), 0, 4);
        grid.add(createPromptText(viewModel.clickY), 1, 4);

        grid.add(createPromptText("Click:"), 0, 5);
        grid.add(createPromptText(viewModel.clickButton), 1, 5);

        return grid;
    }

    private VBox createMouseEventList() {
        ListView<MouseClick> recordedEventsDisplay = new ListView<>(viewModel.recordedClicks);

        recordedEventsDisplay.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Scroll to Last
        recordedEventsDisplay.getItems().addListener(
                (ListChangeListener<MouseClick>) event ->
                        recordedEventsDisplay.scrollTo(recordedEventsDisplay.getItems().size()-1)
        );

        // Remove selected items with DEL
        recordedEventsDisplay.addEventFilter(
                KeyEvent.KEY_PRESSED, e -> {
                    if (e.getCode() == KeyCode.DELETE) {
                        // Cannot use getSelectedItems, since it would also remove non-selected "duplicate" entries
                        for (int index: recordedEventsDisplay.getSelectionModel().getSelectedIndices().reversed()) {
                            viewModel.recordedClicks.remove(index);
                        }
                    }
                }
        );

        return new VBox(4, createPromptText("Recorded Mouse Events"), recordedEventsDisplay);
    }
}
