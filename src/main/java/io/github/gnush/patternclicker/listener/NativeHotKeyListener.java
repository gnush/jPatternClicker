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

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import io.github.gnush.patternclicker.ViewModel;

public class NativeHotKeyListener implements NativeKeyListener {
    private final ViewModel viewModel;

    public NativeHotKeyListener(ViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
        if (viewModel.isReplaying() && nativeEvent.getKeyCode() == NativeKeyEvent.VC_CONTROL) {
            viewModel.stopReplay();
        }

        if (viewModel.isRecording() && nativeEvent.getKeyCode() == NativeKeyEvent.VC_CONTROL) {
            viewModel.stopRecording();
        }
    }
}
