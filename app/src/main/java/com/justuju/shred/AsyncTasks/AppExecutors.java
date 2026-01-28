package com.justuju.shred.AsyncTasks;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AppExecutors {

    //singleton pattern

    private static AppExecutors instance;

    public static AppExecutors getInstance() {
        if (instance == null)
            instance = new AppExecutors();
        return instance;
    }

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

    public ScheduledExecutorService getExecutorService() {
        return executorService;
    }
}
