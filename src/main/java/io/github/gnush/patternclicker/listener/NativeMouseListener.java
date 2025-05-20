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

package io.github.gnush.patternclicker.listener;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;
import io.github.gnush.patternclicker.MouseButtonTranslator;
import io.github.gnush.patternclicker.data.MouseClick;
import io.github.gnush.patternclicker.data.PrimaryDisplayOffset;
import io.github.gnush.patternclicker.ViewModel;
import javafx.scene.input.MouseButton;

public class NativeMouseListener implements NativeMouseInputListener {
    private final ViewModel viewModel;

    private final PrimaryDisplayOffset primaryDisplayOffset;

    private long lastRecordedClickTimestamp = -1;
    private boolean firstClickAfterRecording = true; // to be able to filter (not record) the initial click on start recording, needed since we use release events

    public NativeMouseListener(ViewModel viewModel) {
        this.viewModel = viewModel;

        var displays = GlobalScreen.getNativeMonitors();
        if (displays.length == 0)
            primaryDisplayOffset = new PrimaryDisplayOffset(0, 0);
        else
            primaryDisplayOffset = new PrimaryDisplayOffset(
                    displays[0].getX(),
                    displays[0].getY()
            );
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeEvent) {
        viewModel.mouseX.setValue(nativeEvent.getX() + primaryDisplayOffset.x());
        viewModel.mouseY.setValue(nativeEvent.getY() + primaryDisplayOffset.y());
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeEvent) {
        long now = System.currentTimeMillis();
        long delay = lastRecordedClickTimestamp < 0 ? 0 : Math.max(now-lastRecordedClickTimestamp, 0);

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
            lastRecordedClickTimestamp = now;
        }

        if (firstClickAfterRecording)
            firstClickAfterRecording = false;
    }

    public void resetClickDelay() {
        lastRecordedClickTimestamp = -1;
    }

    public void resetFirstClickRecording() {
        firstClickAfterRecording = true;
    }
}
