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

    // Dashboard summary
    public Map<String, Object> getDashboardStats() throws ExecutionException, InterruptedException {
        List<ReliefRequest> allRequests = getAllRequests(); // Already excludes Rejected
        List<User> volunteers = getAllVolunteers();
        List<InventoryItem> inventory = getAllInventory();

        long pendingCount = allRequests.stream().filter(r -> "Pending".equals(r.getStatus())).count();
        long criticalStock = inventory.stream().filter(i -> i.getQuantity() <= 10).count();
        // Assuming "Active" means Approved but not yet Completed
        long activeMissions = allRequests.stream().filter(r -> "Approved".equals(r.getStatus())).count();

        return Map.of(
                "pendingRequests", pendingCount,
                "activeMissions", activeMissions,
                "totalVolunteers", volunteers.size(),
                "criticalItems", criticalStock,
                "topRequests", allRequests.stream()
                        .filter(r -> "Pending".equals(r.getStatus()))
                        .sorted((a, b) -> b.getUrgency().compareTo(a.getUrgency())) // Simple urgency sort
                        .limit(3)
                        .collect(Collectors.toList()));
    }

    // --- REQUEST CONTROL ---

    public List<ReliefRequest> getAllRequests() throws ExecutionException, InterruptedException {
        // Modify query to filter out "Rejected" requests
        // Using whereNotEqualTo ensures the Admin only sees active or completed tasks
        return firestore.collection("requests")
                .whereNotEqualTo("status", "Rejected")
                .get()
                .get()
                .getDocuments()
                .stream()
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