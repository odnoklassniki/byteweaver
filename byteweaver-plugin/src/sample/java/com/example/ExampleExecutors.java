package com.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExampleExecutors {
    public static void main(String... args) {
        Executors.newCachedThreadPool();
        Executors.newFixedThreadPool(1);
    }

    public static ExecutorService customCachedThreadPool() {
        return Executors.newCachedThreadPool();
    }

    public static ExecutorService customFixedThreadPool(int poolSize) {
        return Executors.newFixedThreadPool(poolSize);
    }
}
