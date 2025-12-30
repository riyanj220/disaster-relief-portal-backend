package com.scd_project.disaster_relief_portal_backend.util;

import java.util.Stack;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Generics, ADTs, and Multithreading. Concept: A background engine that
 * "validates" resources before database commit.
 */

public class ResourceProcessingEngine<T> {

    // Abstract Data Types (ADT)
    private final Stack<T> logs = new Stack<>();
    private final Queue<T> processingQueue = new LinkedList<>();

    // Background processing using Concurrency and Threading.
    public void processResource(T resource, Runnable onComplete) {
        processingQueue.add(resource);

        // Multithreading: Start a new thread to simulate async work
        Thread processThread = new Thread(() -> {
            try {
                System.out.println("[Thread Started] Processing resource: " + resource.getClass().getSimpleName());

                // Multithreading: Sleep to heavy computation/validation
                Thread.sleep(1000);

                synchronized (logs) {
                    logs.push(resource); // ADT: Stack usage
                }

                onComplete.run();

                System.out.println("[Thread Success] Processing complete.");

            } catch (InterruptedException e) {
                System.err.println("[Thread Error] Interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        });

        processThread.start(); // Multithreading: Start method
    }
}