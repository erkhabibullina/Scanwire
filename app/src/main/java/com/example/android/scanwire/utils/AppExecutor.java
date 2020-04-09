package com.example.android.scanwire.utils;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AppExecutor {

    public static final String TAG = AppExecutor.class.getSimpleName();
    private final ScheduledThreadPoolExecutor scheduled;
    private final ExecutorService dbThread;
    private final Executor analyzerThread;
    private final Executor detectorThread;
    private final Executor mainThread;

    @Inject
    public AppExecutor(ScheduledThreadPoolExecutor scheduled,
                        ExecutorService dbThread,
                       Executor analyzerThread,
                       Executor detectorThread,
                       Executor mainThread) {
        this.scheduled = scheduled;
        this.dbThread = dbThread;
        this.analyzerThread = analyzerThread;
        this.detectorThread = detectorThread;
        this.mainThread = mainThread;
    }

    public ScheduledThreadPoolExecutor scheduled() {
        return scheduled;
    }

    public Executor dbThread() {
        return dbThread;
    }

    public Executor analyzerThread() {
        return analyzerThread;
    }

    public Executor detectorThread() {
        return detectorThread;
    }

    public Executor mainThread() {
        return mainThread;
    }

    public <T> T dbFuture(Callable<T> callable) {
        Future<T> future = dbThread.submit(callable);
        T obj = null;
        try {
            obj = future.get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            future.cancel(true);
        }
        return obj;
    }

    public static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}