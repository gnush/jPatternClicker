package io.github.gnush.patternclicker;

import javafx.beans.property.*;
import javafx.collections.FXCollections;

public class ViewModel {
    private static final String START_RECORD_TEXT = "Start Recording";
    private static final String STOP_RECORD_TEXT = "Stop Recording";

    private static final String START_REPLAY_TEXT = "Replay Pattern";
    private static final String STOP_REPLAY_TEXT = "Stop (<ESC>)";

    public final DoubleProperty mouseX = new SimpleDoubleProperty(0.0d);
    public final DoubleProperty mouseY = new SimpleDoubleProperty(0.0d);

    public final DoubleProperty clickX = new SimpleDoubleProperty(0.0d);
    public final DoubleProperty clickY = new SimpleDoubleProperty(0.0d);
    public final StringProperty clickButton = new SimpleStringProperty("");

    public final ListProperty<MouseEvent> recordedClicks = new SimpleListProperty<>(FXCollections.observableArrayList());

    private boolean replaying = false;
    private final StringProperty replayButtonText = new SimpleStringProperty(START_REPLAY_TEXT);

    private boolean recording = false;
    private final StringProperty recordButtonText = new SimpleStringProperty(START_RECORD_TEXT);

    public final PrimaryMonitorOffset primaryMonitorOffset;

    public ViewModel(PrimaryMonitorOffset primaryMonitorOffset) {
        this.primaryMonitorOffset = primaryMonitorOffset;
    }

    public void startReplay() {
        replaying = true;
        replayButtonText.setValue(STOP_REPLAY_TEXT);
    }

    public void stopReplay() {
        replaying = false;
        replayButtonText.setValue(START_REPLAY_TEXT);
    }

    public void toggleReplay() {
        if (replaying)
            stopReplay();
        else
            startReplay();
    }

    public boolean isReplaying() {
        return replaying;
    }

    public StringProperty replayButtonProperty() {
        return replayButtonText;
    }

    public void startRecording() {
        recording = true;
        recordButtonText.setValue(STOP_RECORD_TEXT);
    }

    public void stopRecording() {
        recording = false;
        recordButtonText.setValue(START_RECORD_TEXT);
    }

    public void toggleRecording() {
        if (recording)
            stopRecording();
        else
            startRecording();
    }

    public boolean isRecording() {
        return recording;
    }

    public StringProperty recordButtonProperty() {
        return recordButtonText;
    }
}
