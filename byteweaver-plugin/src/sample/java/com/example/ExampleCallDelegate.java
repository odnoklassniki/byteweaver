package com.example;

public class ExampleCallDelegate {
    public static boolean replaceSomeOtherMethodCalled;
    public static boolean afterMethodCalled;
    public static boolean beforeMethodCalled;

    public static void beforeMethod() {
        beforeMethodCalled = true;
    }

    public static void afterMethod() {
        afterMethodCalled = true;
    }

    public static void replaceSomeOtherMethod(ExampleCall self) {
        replaceSomeOtherMethodCalled = true;
    }
}
