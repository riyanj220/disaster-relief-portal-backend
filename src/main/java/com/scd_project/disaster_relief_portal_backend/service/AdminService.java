package com.scd_project.disaster_relief_portal_backend.service;

import com.google.cloud.firestore.*;
import com.scd_project.disaster_relief_portal_backend.model.InventoryItem;
import com.scd_project.disaster_relief_portal_backend.model.ReliefRequest;
import com.scd_project.disaster_relief_portal_backend.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final Firestore firestore;

    // --- REQUEST CONTROL ---

    public List<ReliefRequest> getAllRequests() throws ExecutionException, InterruptedException {
        return firestore.collection("requests").get().get().getDocuments().stream()
                .map(doc -> doc.toObject(ReliefRequest.class))
                .collect(Collectors.toList());
    }

    public String updateRequestStatus(String requestId, Map<String, Object> updates)
            throws ExecutionException, InterruptedException {
        // Updates can include "status" and "assignedVolunteerId"
        firestore.collection("requests").document(requestId).update(updates).get();
        return "Request " + requestId + " updated successfully.";
    }

    // --- INVENTORY MANAGEMENT ---

    public String addInventoryItem(InventoryItem item) throws ExecutionException, InterruptedException {
        String id = UUID.randomUUID().toString();
        item.setItemId(id);
        firestore.collection("inventory").document(id).set(item).get();
        return id;
    }

    public List<InventoryItem> getAllInventory() throws ExecutionException, InterruptedException {
        return firestore.collection("inventory").get().get().getDocuments().stream()
                .map(doc -> doc.toObject(InventoryItem.class))
                .collect(Collectors.toList());
    }

    // --- VOLUNTEER MANAGEMENT ---

    public List<User> getAllVolunteers() throws ExecutionException, InterruptedException {
        // Filter users collection where role == "VOLUNTEER"
        return firestore.collection("users")
                .whereEqualTo("role", "VOLUNTEER")
                .get().get().getDocuments().stream()
                .map(doc -> doc.toObject(User.class))
                .collect(Collectors.toList());
    }

    public String removeVolunteer(String volunteerId) {
        firestore.collection("users").document(volunteerId).delete();
        return "Volunteer " + volunteerId + " removed from network.";
    }
}