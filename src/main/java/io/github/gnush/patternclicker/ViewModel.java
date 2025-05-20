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

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.scene.robot.Robot;

public class ViewModel {
    private Task<Void> task;

    private static final String START_RECORD_TEXT = "⏺ Record";
    private static final String STOP_RECORD_TEXT = "⏹ <CTRL>";

    private static final String START_REPLAY_TEXT = "⏵ Replay";
    private static final String STOP_REPLAY_TEXT = "⏹ <CTRL>";

    public final DoubleProperty mouseX = new SimpleDoubleProperty(0.0d);
    public final DoubleProperty mouseY = new SimpleDoubleProperty(0.0d);

    public final DoubleProperty clickX = new SimpleDoubleProperty(0.0d);
    public final DoubleProperty clickY = new SimpleDoubleProperty(0.0d);
    public final StringProperty clickButton = new SimpleStringProperty("");

    public final long defaultRepetitionDelay = 1000L;
    public final StringProperty repetitionDelay = new SimpleStringProperty(String.valueOf(defaultRepetitionDelay));

    public final ListProperty<MouseClick> recordedClicks = new SimpleListProperty<>(FXCollections.observableArrayList());

    private boolean replaying = false;
    private final StringProperty replayButtonText = new SimpleStringProperty(START_REPLAY_TEXT);

    private boolean recording = false;
    private final StringProperty recordButtonText = new SimpleStringProperty(START_RECORD_TEXT);

    public void startReplay() {
        replaying = true;
        replayButtonText.setValue(STOP_REPLAY_TEXT);

        task = new Task<>() {
            final Robot bot = new Robot();

            @SuppressWarnings("BusyWait")
            @Override
            protected Void call() {
                while (!isCancelled()) {
                    for (MouseClick click: recordedClicks) {
                        try {
                            Thread.sleep(click.delayMillis());
                        } catch (InterruptedException ignored) {
                            break;
                        }

                        Platform.runLater(() -> {
                            bot.mouseMove(click.asPoint());
                            bot.mouseClick(click.button());
                        });
                    }

                    try {
                        Thread.sleep(loopDelay());
                    } catch (InterruptedException ignored) {
                        break;
                    }
                }

                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public void stopReplay() {
        replaying = false;
        replayButtonText.setValue(START_REPLAY_TEXT);

        if (task != null && task.isRunning())
            task.cancel();
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

    private long loopDelay() {
        long delay = defaultRepetitionDelay;
        try {
            delay = Long.parseLong(repetitionDelay.getValue());
        } catch (NumberFormatException ignored) { }

        return delay;
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
