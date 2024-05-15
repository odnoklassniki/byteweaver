package ru.ok.android.prefs;

import android.content.Context;
import android.content.SharedPreferences;

public class FastSharedPreferences {
    public SharedPreferences getSharedPreferences(Context context, String name, int mode) {
        return context.getSharedPreferences(name, mode);
    }
}
