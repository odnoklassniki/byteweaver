package android.app;

import android.content.Context;
import android.os.Bundle;

public class Activity extends Context {
    // need some method with parameter
    public void onCreate(Bundle savedInstanceState) {
    }

    // need some method to return boolean
    public boolean onNavigateUp() {
        return false;
    }
}
