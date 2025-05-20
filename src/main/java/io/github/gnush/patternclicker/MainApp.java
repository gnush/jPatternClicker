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
import io.github.gnush.patternclicker.listener.NativeMouseListener;
import io.github.gnush.patternclicker.listener.NativeHotKeyListener;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

// TODO
//  - save button to save the recording
//  - load button to load a saved recording
public class MainApp extends Application {
    private final ViewModel viewModel = new ViewModel();

    @Override
    public void start(Stage stage) {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            System.err.println("Couldn't register native hook");
            System.err.println(e.getMessage());
            System.exit(1);
        }

        GlobalScreen.setEventDispatcher(new PlatformDispatchService());

        var clickListener = new NativeMouseListener(viewModel);

        GlobalScreen.addNativeMouseMotionListener(clickListener);
        GlobalScreen.addNativeMouseListener(clickListener);
        GlobalScreen.addNativeKeyListener(new NativeHotKeyListener(viewModel));

        // record action
        Region root = new ScreenBuilder(
                viewModel,
                // record action
                clickListener::resetFirstClickRecording,
                () -> { // replay action
                },
                () -> { // clear action
                    viewModel.recordedClicks.clear();
                    clickListener.resetClickDelay();
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