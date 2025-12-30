package com.scd_project.disaster_relief_portal_backend.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.scd_project.disaster_relief_portal_backend.model.ReliefRequest;
import com.scd_project.disaster_relief_portal_backend.model.ImmutableAuditLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final Firestore firestore;

    // A Queue to manage the "flow" of incoming requests
    private final Queue<ReliefRequest> submissionQueue = new LinkedList<>();

    public String submitRequest(ReliefRequest reliefRequest) throws ExecutionException, InterruptedException {
        // 1. ADT & Mutability: Add the mutable object to our ADT Queue
        submissionQueue.add(reliefRequest);

        // 2. Immutability : Create an unchangeable record of this submission
        ImmutableAuditLog audit = new ImmutableAuditLog(
                UUID.randomUUID().toString(),
                "SUBMIT_AID_REQUEST",
                reliefRequest.getCitizenId());
        System.out.println("[Audit Log Created] Fixed ID: " + audit.getLogId());

        String id = UUID.randomUUID().toString();
        reliefRequest.setRequestId(id);
        reliefRequest.setStatus("Pending");
        reliefRequest.setTimestamp(System.currentTimeMillis());

        firestore.collection("requests").document(id).set(reliefRequest).get();

        submissionQueue.poll();

        return id;
    }

    public List<ReliefRequest> getCitizenHistory(String citizenId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection("requests")
                .whereEqualTo("citizenId", citizenId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get();

        return future.get().getDocuments().stream()
                .map(doc -> doc.toObject(ReliefRequest.class))
                .collect(Collectors.toList());
    }
}