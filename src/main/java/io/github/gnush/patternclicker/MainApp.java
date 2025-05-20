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

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

// TODO
//  - save button to save the recording
//  - load button to load a saved recording
public class MainApp extends Application implements NativeMouseInputListener, NativeKeyListener {
    private boolean firstClickAfterRecording = true; // to be able to filter (not record) the initial click on start recording, needed since we use release events
    private long lastRecordedClickTimestamp = -1;

    ViewModel viewModel = new ViewModel();

    PrimaryMonitorOffset primaryDisplayOffset = new PrimaryMonitorOffset(0, 0);

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
        if (viewModel.isReplaying() && nativeEvent.getKeyCode() == NativeKeyEvent.VC_CONTROL) {
            viewModel.stopReplay();
        }

        if (viewModel.isRecording() && nativeEvent.getKeyCode() == NativeKeyEvent.VC_CONTROL) {
            viewModel.stopRecording();
        }
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeEvent) {
        viewModel.mouseX.setValue(nativeEvent.getX() + primaryDisplayOffset.x());
        viewModel.mouseY.setValue(nativeEvent.getY() + primaryDisplayOffset.y());
    }

    // LIMITATIONS:
    // - native click event not super reliable, detect release events (may track press and release events later to support long clicks)
    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeEvent) {
        long delay = lastRecordedClickTimestamp <0 ? 0 : Math.max(System.currentTimeMillis()- lastRecordedClickTimestamp, 0);

        var event = new MouseClick(
                MouseButtonTranslator.fromNative(nativeEvent.getButton()).orElse(MouseButton.PRIMARY),
                nativeEvent.getX() + primaryDisplayOffset.x(),
                nativeEvent.getY() + primaryDisplayOffset.y(),
                delay
        );

        viewModel.clickX.setValue(event.x());
        viewModel.clickY.setValue(event.y());
        viewModel.clickButton.setValue(event.button().name());

        if (viewModel.isRecording() && !firstClickAfterRecording) {
            viewModel.recordedClicks.add(event);
            lastRecordedClickTimestamp = System.currentTimeMillis();
        }

        if (firstClickAfterRecording)
            firstClickAfterRecording = false;
    }

    @Override
    public void start(Stage stage) {
        primaryDisplayOffset = new PrimaryMonitorOffset(
                GlobalScreen.getNativeMonitors()[0].getX(),
                GlobalScreen.getNativeMonitors()[0].getY()
        );

        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            System.err.println("Couldn't register native hook");
            System.err.println(e.getMessage());
            System.exit(1);
        }

        GlobalScreen.setEventDispatcher(new PlatformDispatchService());

        GlobalScreen.addNativeMouseMotionListener(this);
        GlobalScreen.addNativeMouseListener(this);
        GlobalScreen.addNativeKeyListener(this);

        // Offset of the primary display (when another display is arranged left of the primary one)
        if (GlobalScreen.getNativeMonitors().length > 0) {
            viewModel = new ViewModel();
        }

        Region root = new ScreenBuilder(
                viewModel,
                () -> { // record action
                    firstClickAfterRecording = true;
                },
                () -> { // replay action
                },
                () -> { // clear action
                    viewModel.recordedClicks.clear();
                    lastRecordedClickTimestamp = -1;
                }
        ).build();

        Scene scene = new Scene(root);

        final var stylesheet = getClass().getResource("style.css");
        if (stylesheet != null)
            scene.getStylesheets().add(stylesheet.toExternalForm());

        stage.setTitle("jPatternClicker");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        viewModel.stopReplay();

        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            System.err.println("Couldn't deregister native hook");
            System.err.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}