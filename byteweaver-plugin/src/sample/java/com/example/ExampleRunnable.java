package com.example;

public class ExampleRunnable implements Runnable {
    @Override
    public void run() {
        while (true) {
            System.out.println("Running");
        }
    }
}
