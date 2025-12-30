package com.scd_project.disaster_relief_portal_backend.service;

import com.google.cloud.firestore.*;
import com.scd_project.disaster_relief_portal_backend.model.ReliefRequest;
import com.scd_project.disaster_relief_portal_backend.util.TaskVerificationSystem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VolunteerService {

    private final Firestore firestore;

    // for Inter-thread communication
    private final TaskVerificationSystem<String> verificationSystem = new TaskVerificationSystem<>();

    public Map<String, Object> getVolunteerStats(String volunteerId) throws ExecutionException, InterruptedException {
        List<ReliefRequest> tasks = getAssignedTasks(volunteerId);

        long completedCount = tasks.stream().filter(t -> "Completed".equals(t.getStatus())).count();
        double totalHours = tasks.stream().mapToDouble(ReliefRequest::getHoursSpent).sum();

        ReliefRequest activeAssignment = tasks.stream()
                .filter(t -> !"Completed".equals(t.getStatus()))
                .sorted((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()))
                .findFirst()
                .orElse(null);

        return Map.of(
                "completedTasks", completedCount,
                "totalHours", totalHours,
                "activeAssignment", activeAssignment != null ? activeAssignment : "none");
    }

    public String updateStatus(String userId, String status) throws ExecutionException, InterruptedException {
        DocumentReference userRef = firestore.collection("users").document(userId);
        userRef.update("status", status).get();
        return "Status updated to: " + status;
    }

    public List<ReliefRequest> getAssignedTasks(String volunteerId) throws ExecutionException, InterruptedException {
        List<ReliefRequest> tasks = firestore.collection("requests")
                .whereEqualTo("assignedVolunteerId", volunteerId)
                .get().get().getDocuments().stream()
                .map(doc -> doc.toObject(ReliefRequest.class))
                .collect(Collectors.toList());

        return tasks.stream()
                .sorted((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()))
                .collect(Collectors.toList());
    }

    /**
     * Inter-Thread Communication (Wait/Notify).
     */
    public String updateTaskStatus(String requestId, String status) throws ExecutionException, InterruptedException {

        // Start a verification thread
        new Thread(() -> {
            verificationSystem.verifyTask(requestId);
        }).start();

        // Main thread waits for the verification thread to notify it
        verificationSystem.waitForVerification();

        // Once notified, proceed to DB update
        DocumentReference requestRef = firestore.collection("requests").document(requestId);
        requestRef.update("status", status).get();

        return "Task " + requestId + " verified and marked as " + status;
    }
}