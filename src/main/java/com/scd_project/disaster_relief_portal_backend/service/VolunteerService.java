package com.scd_project.disaster_relief_portal_backend.service;

import com.google.cloud.firestore.*;
import com.scd_project.disaster_relief_portal_backend.model.ReliefRequest;
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

    // 1. Update availabilityStatus in the "users" collection
    public String updateStatus(String userId, String status) throws ExecutionException, InterruptedException {
        // status should be "ON_DUTY" or "OFF_DUTY"
        DocumentReference userRef = firestore.collection("users").document(userId);
        userRef.update("status", status).get();

        return "Status updated to: " + status;
    }

    public List<ReliefRequest> getAssignedTasks(String volunteerId) throws ExecutionException, InterruptedException {
        // 1. Fetch tasks assigned to this volunteer
        List<ReliefRequest> tasks = firestore.collection("requests")
                .whereEqualTo("assignedVolunteerId", volunteerId)
                .get()
                .get()
                .getDocuments()
                .stream()
                .map(doc -> doc.toObject(ReliefRequest.class))
                .collect(Collectors.toList());

        // 2. Perform a descending sort by timestamp (latest first)
        return tasks.stream()
                .sorted((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()))
                .collect(Collectors.toList());
    }

    public String updateTaskStatus(String requestId, String status) throws ExecutionException, InterruptedException {
        // requestId is the ID of the ReliefRequest
        DocumentReference requestRef = firestore.collection("requests").document(requestId);
        requestRef.update("status", status).get(); // Update the status field

        return "Task " + requestId + " marked as " + status;
    }
}