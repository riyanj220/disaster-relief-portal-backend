package com.scd_project.disaster_relief_portal_backend.util;

// Inter-Thread Communication (Wait/Notify).
public class TaskVerificationSystem<T> {
    private boolean isVerified = false;

    // The main thread calls this to wait for verification.
    public synchronized void waitForVerification() {
        while (!isVerified) {
            try {
                System.out.println("[Main Thread] Waiting for mission verification...");
                wait(); // Thread method: Wait
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        isVerified = false; // Reset for next use
        System.out.println("[Main Thread] Verification received. Proceeding with DB commit.");
    }

    // A background thread calls this to notify completion.
    public synchronized void verifyTask(T taskInfo) {
        System.out.println("[Verification Thread] Auditing mission: " + taskInfo);
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isVerified = true;
        notify();
    }
}