package com.example;

public class ExampleCall {
    public boolean someOtherMethodCalled = false;

    public void method() {
        someOtherMethod();
    }

    public void someOtherMethod() {
        someOtherMethodCalled = true;
    }
}
