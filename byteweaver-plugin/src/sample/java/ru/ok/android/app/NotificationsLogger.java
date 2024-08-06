package ru.ok.android.app;

import android.app.Notification;
import android.app.Service;

public class NotificationsLogger {
    public static void logStartForeground(Object self, int id, Notification notification) {
        if (self instanceof Service selfService) {
            selfService.startForeground(id, notification);
        } else {
            throw new UnsupportedOperationException("Don't know how to call startForeground on " + self.getClass());
        }
    }
}
