package com.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExampleExecutors {
    public static void main(String... args) {
        Executors.newCachedThreadPool();
    }

    public static ExecutorService customCachedThreadPool(Class<Executors> klass) {
        return Executors.newCachedThreadPool();
    }
}
