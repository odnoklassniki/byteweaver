package com.example;

import android.app.Notification;
import android.app.NotificationManager;

public class ExampleNotification {
   public void showNotification(NotificationManager manager, Notification notification) {
      manager.notify(0, notification);
      manager.notify("", 0, notification);
   }
}
