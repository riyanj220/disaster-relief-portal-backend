package com.scd_project.disaster_relief_portal_backend.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.scd_project.disaster_relief_portal_backend.model.ReliefRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VolunteerService {

    private final Firestore firestore;

    // 1. Update availabilityStatus in the "users" collection
    public String updateStatus(String userId, String status) throws ExecutionException, InterruptedException {
        // status should be "ON_DUTY" or "OFF_DUTY"
        DocumentReference userRef = firestore.collection("users").document(userId);
        userRef.update("status", status).get();

        return "Status updated to: " + status;
    }

    // 2. Fetch requests assigned to this specific volunteer
    public List<ReliefRequest> getAssignedTasks(String volunteerId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection("requests")
                .whereEqualTo("assignedVolunteerId", volunteerId)
                .get();

        return future.get().getDocuments().stream()
                .map(doc -> doc.toObject(ReliefRequest.class))
                .collect(Collectors.toList());
    }
}