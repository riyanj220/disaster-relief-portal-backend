package com.scd_project.disaster_relief_portal_backend.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.scd_project.disaster_relief_portal_backend.model.ReliefRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final Firestore firestore;

    public String submitRequest(ReliefRequest reliefRequest) throws ExecutionException, InterruptedException {
        // Auto-generate a unique ID for the request
        String id = UUID.randomUUID().toString();
        reliefRequest.setRequestId(id);
        reliefRequest.setStatus("Pending");
        reliefRequest.setTimestamp(System.currentTimeMillis());

        // Save to "requests" collection
        firestore.collection("requests").document(id).set(reliefRequest).get();
        return id;
    }

    /**
     * Fetches all requests where citizenId matches the provided ID.
     * We use .whereEqualTo() to act as our "Foreign Key" filter.
     */
    public List<ReliefRequest> getCitizenHistory(String citizenId) throws ExecutionException, InterruptedException {
        // Query the "requests" collection
        ApiFuture<QuerySnapshot> future = firestore.collection("requests")
                .whereEqualTo("citizenId", citizenId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get();

        // Convert the Firestore documents into a List of ReliefRequest objects
        return future.get().getDocuments().stream()
                .map(doc -> doc.toObject(ReliefRequest.class))
                .collect(Collectors.toList());
    }
}