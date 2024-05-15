package com.example;

import android.content.Context;
import android.content.SharedPreferences;

public class ExamplePreferences {
    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("prefs", 0x0);
    }
}
