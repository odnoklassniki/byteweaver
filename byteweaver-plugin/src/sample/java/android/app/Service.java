package android.app;

import android.content.Intent;

public abstract class Service {
    public final void startForeground(int id, Notification notification) {
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return 0;
    }
}
