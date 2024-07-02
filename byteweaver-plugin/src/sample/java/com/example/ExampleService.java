package com.example;

import android.app.Notification;
import android.content.Intent;

import dagger.android.DaggerService;

public class ExampleService extends DaggerService {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(10, new Notification());
        return 0;
    }
}
