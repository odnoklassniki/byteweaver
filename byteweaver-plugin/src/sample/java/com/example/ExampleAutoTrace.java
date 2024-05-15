package com.example;

import ru.ok.android.commons.os.AutoTrace;

public class ExampleAutoTrace {
    @AutoTrace
    void doNothing() {
        try {
            System.out.println();
        } catch (Exception ex) {
            System.out.println();
        }
    }
}
