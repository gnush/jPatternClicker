package io.github.gnush.patternclicker;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
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
        HBox result = new HBox(8);
        result.setAlignment(Pos.CENTER_LEFT);
        result.setPadding(new Insets(2));

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

        result.getChildren().addAll(recordButton, clearButton, replayButton);

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

        grid.add(createPromptText("Mouse X:"), 0, 0);
        grid.add(createPromptText(viewModel.mouseX), 1, 0);
        grid.add(createPromptText("Mouse Y:"), 0, 1);
        grid.add(createPromptText(viewModel.mouseY), 1, 1);

        grid.add(createPromptText("Click X:"), 0, 2);
        grid.add(createPromptText(viewModel.clickX), 1, 2);
        grid.add(createPromptText("Click Y:"), 0, 3);
        grid.add(createPromptText(viewModel.clickY), 1, 3);

        grid.add(createPromptText("Click:"), 0, 4);
        grid.add(createPromptText(viewModel.clickButton), 1, 4);

        return grid;
    }

    private VBox createMouseEventList() {
        ListView<MouseClick> recordedEventsDisplay = new ListView<>();
        recordedEventsDisplay.itemsProperty().bind(viewModel.recordedClicks);
        recordedEventsDisplay.getItems().addListener(
                (ListChangeListener<MouseClick>) event ->
                        recordedEventsDisplay.scrollTo(recordedEventsDisplay.getItems().size()-1)
        );

        return new VBox(4, createPromptText("Recorded Mouse Events"), recordedEventsDisplay);
    }
}
