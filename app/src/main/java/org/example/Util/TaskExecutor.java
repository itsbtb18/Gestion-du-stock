package org.example.util;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class TaskExecutor {
    
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(
        Runtime.getRuntime().availableProcessors(),
        r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName("TaskExecutor-" + t.getId());
            return t;
        }
    );
    
    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(2, r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.setName("TaskScheduler-" + t.getId());
        return t;
    });
    
    private TaskExecutor() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    public static <T> void runAsync(
            Supplier<T> backgroundTask,
            Consumer<T> onSuccess,
            Consumer<Throwable> onError) {
        
        EXECUTOR.submit(() -> {
            try {
                T result = backgroundTask.get();
                Platform.runLater(() -> onSuccess.accept(result));
            } catch (Exception e) {
                LoggerUtil.logError(TaskExecutor.class, "Background task failed", e);
                Platform.runLater(() -> onError.accept(e));
            }
        });
    }
    
    public static void runAsync(
            Runnable backgroundTask,
            Runnable onSuccess,
            Consumer<Throwable> onError) {
        
        EXECUTOR.submit(() -> {
            try {
                backgroundTask.run();
                Platform.runLater(onSuccess);
            } catch (Exception e) {
                LoggerUtil.logError(TaskExecutor.class, "Background task failed", e);
                Platform.runLater(() -> onError.accept(e));
            }
        });
    }
    
    public static <T> void runAsync(Supplier<T> task, Consumer<T> onSuccess) {
        runAsync(task, onSuccess, e -> 
            DialogService.showError("Erreur", "Une erreur s'est produite: " + e.getMessage(), e));
    }
    
    public static void runAsync(Runnable task, Runnable onSuccess) {
        runAsync(task, onSuccess, e -> 
            DialogService.showError("Erreur", "Une erreur s'est produite: " + e.getMessage(), e));
    }
    
    public static <T> Task<T> createTask(
            Supplier<T> backgroundWork,
            Consumer<T> onSuccess,
            Consumer<Throwable> onError) {
        
        Task<T> task = new Task<>() {
            @Override
            protected T call() throws Exception {
                return backgroundWork.get();
            }
        };
        
        task.setOnSucceeded(e -> onSuccess.accept(task.getValue()));
        task.setOnFailed(e -> onError.accept(task.getException()));
        
        EXECUTOR.submit(task);
        return task;
    }
    
    public static ScheduledFuture<?> schedule(Runnable task, long delayMs) {
        return SCHEDULER.schedule(() -> Platform.runLater(task), delayMs, TimeUnit.MILLISECONDS);
    }
    
    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long initialDelayMs, long periodMs) {
        return SCHEDULER.scheduleAtFixedRate(
            () -> Platform.runLater(task), 
            initialDelayMs, 
            periodMs, 
            TimeUnit.MILLISECONDS
        );
    }
    
    public static void runOnUiThread(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }
    
    public static void runOnUiThreadAndWait(Runnable action) throws InterruptedException {
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() -> {
                try {
                    action.run();
                } finally {
                    latch.countDown();
                }
            });
            latch.await();
        }
    }
    
    public static <T> Future<T> submit(Callable<T> task) {
        return EXECUTOR.submit(task);
    }
    
    public static boolean isOnUiThread() {
        return Platform.isFxApplicationThread();
    }
    
    public static void shutdown() {
        EXECUTOR.shutdown();
        SCHEDULER.shutdown();
        try {
            if (!EXECUTOR.awaitTermination(5, TimeUnit.SECONDS)) {
                EXECUTOR.shutdownNow();
            }
            if (!SCHEDULER.awaitTermination(5, TimeUnit.SECONDS)) {
                SCHEDULER.shutdownNow();
            }
        } catch (InterruptedException e) {
            EXECUTOR.shutdownNow();
            SCHEDULER.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    public static class AsyncBuilder<T> {
        private Supplier<T> task;
        private Consumer<T> onSuccess;
        private Consumer<Throwable> onError;
        private Runnable onFinally;
        private String loadingMessage;
        
        public AsyncBuilder<T> task(Supplier<T> task) {
            this.task = task;
            return this;
        }
        
        public AsyncBuilder<T> onSuccess(Consumer<T> onSuccess) {
            this.onSuccess = onSuccess;
            return this;
        }
        
        public AsyncBuilder<T> onError(Consumer<Throwable> onError) {
            this.onError = onError;
            return this;
        }
        
        public AsyncBuilder<T> onFinally(Runnable onFinally) {
            this.onFinally = onFinally;
            return this;
        }
        
        public AsyncBuilder<T> withLoading(String message) {
            this.loadingMessage = message;
            return this;
        }
        
        public void execute() {
            if (task == null) {
                throw new IllegalStateException("Task must be set");
            }
            
            DialogService.ProgressDialog progress = null;
            if (loadingMessage != null) {
                progress = DialogService.showProgress("Chargement", loadingMessage);
                progress.show();
            }
            
            final DialogService.ProgressDialog finalProgress = progress;
            
            EXECUTOR.submit(() -> {
                try {
                    T result = task.get();
                    Platform.runLater(() -> {
                        if (finalProgress != null) finalProgress.close();
                        if (onSuccess != null) onSuccess.accept(result);
                        if (onFinally != null) onFinally.run();
                    });
                } catch (Exception e) {
                    LoggerUtil.logError(TaskExecutor.class, "Async task failed", e);
                    Platform.runLater(() -> {
                        if (finalProgress != null) finalProgress.close();
                        if (onError != null) {
                            onError.accept(e);
                        } else {
                            DialogService.showError("Erreur", e.getMessage(), e);
                        }
                        if (onFinally != null) onFinally.run();
                    });
                }
            });
        }
    }
    
    public static <T> AsyncBuilder<T> async() {
        return new AsyncBuilder<>();
    }
}
