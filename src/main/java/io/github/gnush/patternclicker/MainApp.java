package io.github.gnush.patternclicker;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

// TODO
//  - explore exception on closing after replaying
//  - save button to save the recording
//  - load button to load a saved recording
public class MainApp extends Application implements NativeMouseInputListener, NativeKeyListener {
    private boolean firstClickAfterRecording = true; // to be able to filter (not record) the initial click on start recording, needed since we use release events
    private long lastRecordedClickTimestamp = -1;

    ViewModel viewModel = new ViewModel(new PrimaryMonitorOffset(0, 0));

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
        if (viewModel.isReplaying() && nativeEvent.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
            viewModel.stopReplay();
        }
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeEvent) {
        viewModel.mouseX.setValue(nativeEvent.getX() + viewModel.primaryMonitorOffset.x());
        viewModel.mouseY.setValue(nativeEvent.getY() + viewModel.primaryMonitorOffset.y());
    }

    // LIMITATIONS:
    // - native click event not super reliable, detect release events (may track press and release events later to support long clicks)
    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeEvent) {
        long delay = lastRecordedClickTimestamp <0 ? 0 : Math.max(System.currentTimeMillis()- lastRecordedClickTimestamp, 0);

        var event = new MouseClick(
                MouseButtonTranslator.fromNative(nativeEvent.getButton()).orElse(MouseButton.PRIMARY),
                nativeEvent.getX() + viewModel.primaryMonitorOffset.x(),
                nativeEvent.getY() + viewModel.primaryMonitorOffset.y(),
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
            viewModel = new ViewModel(new PrimaryMonitorOffset(
                    GlobalScreen.getNativeMonitors()[0].getX(),
                    GlobalScreen.getNativeMonitors()[0].getY()
            ));
        }

        Region root = new ScreenBuilder(
                viewModel,
                () -> { // record action
                    firstClickAfterRecording = true;
                },
                () -> { // replay action
                    Robot bot = new Robot();
                    long delay = 1000L;

                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            if (viewModel.isReplaying()) {
                                viewModel.recordedClicks.forEach(click -> {
                                    try {
                                        Thread.sleep(click.delayMillis());
                                    } catch (InterruptedException ignored) {}

                                    Platform.runLater(() -> {
                                        bot.mouseMove(new Point2D(click.x(), click.y()));
                                        bot.mouseClick(click.button());
                                    });
                                });
                                try {
                                    Thread.sleep(delay);
                                } catch (InterruptedException ignored) {}
                            }
                        }
                    };

                    Timer timer = new Timer("pattern replay");
                    timer.scheduleAtFixedRate(task, 0, delay);
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