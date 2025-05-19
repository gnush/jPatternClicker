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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

public class PlatformDispatchService extends AbstractExecutorService {
    private boolean running = true;

    @Override
    public void shutdown() {
        running = false;
    }

    @Override
    public List<Runnable> shutdownNow() {
        running = false;
        return new ArrayList<>(0);
    }

    @Override
    public boolean isShutdown() {
        return !running;
    }

    @Override
    public boolean isTerminated() {
        return !running;
    }

    @Override
    public boolean awaitTermination(long l, TimeUnit timeUnit) {
        return true;
    }

    @Override
    public void execute(Runnable runnable) {
        Platform.runLater(runnable);
    }
}
