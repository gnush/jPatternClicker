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
