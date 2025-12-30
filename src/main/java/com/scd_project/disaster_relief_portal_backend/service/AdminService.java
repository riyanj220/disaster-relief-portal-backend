package com.scd_project.disaster_relief_portal_backend.service;

import com.google.cloud.firestore.*;
import com.scd_project.disaster_relief_portal_backend.model.InventoryItem;
import com.scd_project.disaster_relief_portal_backend.model.ReliefRequest;
import com.scd_project.disaster_relief_portal_backend.model.User;
import com.scd_project.disaster_relief_portal_backend.util.ResourceProcessingEngine;
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

    // Generics
    private final ResourceProcessingEngine<InventoryItem> inventoryEngine = new ResourceProcessingEngine<>();
    private final ResourceProcessingEngine<Map<String, Object>> requestUpdateEngine = new ResourceProcessingEngine<>();

    public Map<String, Object> getDashboardStats() throws ExecutionException, InterruptedException {
        List<ReliefRequest> allRequests = getAllRequests();
        List<User> volunteers = getAllVolunteers();
        List<InventoryItem> inventory = getAllInventory();

        long pendingCount = allRequests.stream().filter(r -> "Pending".equals(r.getStatus())).count();
        long criticalStock = inventory.stream().filter(i -> i.getQuantity() <= 10).count();
        long activeMissions = allRequests.stream().filter(r -> "Approved".equals(r.getStatus())).count();

        return Map.of(
                "pendingRequests", pendingCount,
                "activeMissions", activeMissions,
                "totalVolunteers", volunteers.size(),
                "criticalItems", criticalStock,
                "topRequests", allRequests.stream()
                        .filter(r -> "Pending".equals(r.getStatus()))
                        .sorted((a, b) -> b.getUrgency().compareTo(a.getUrgency()))
                        .limit(3)
                        .collect(Collectors.toList()));
    }

    public List<ReliefRequest> getAllRequests() throws ExecutionException, InterruptedException {
        List<ReliefRequest> requests = firestore.collection("requests")
                .whereNotEqualTo("status", "Rejected")
                .orderBy("status")
                .get().get().getDocuments().stream()
                .map(doc -> doc.toObject(ReliefRequest.class))
                .collect(Collectors.toList());

        return requests.stream()
                .sorted((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()))
                .collect(Collectors.toList());
    }

    // Threading and Concurrency via Resourse Processing Engine.
    public String updateRequestStatus(String requestId, Map<String, Object> updates)
            throws ExecutionException, InterruptedException {

        requestUpdateEngine.processResource(updates, () -> {
            try {
                firestore.collection("requests").document(requestId).update(updates).get();
            } catch (Exception e) {
                System.err.println("Async Request Update Failed");
            }
        });

        return "Request " + requestId + " processing in background thread.";
    }

    // Threading, Generics and ADT's via Resourse Processing Engine.
    public String addInventoryItem(InventoryItem item) throws ExecutionException, InterruptedException {
        String id = UUID.randomUUID().toString();
        item.setItemId(id);

        inventoryEngine.processResource(item, () -> {
            try {
                firestore.collection("inventory").document(id).set(item).get();
            } catch (Exception e) {
                System.err.println("Async Inventory Save Failed");
            }
        });

        return id;
    }

    public List<InventoryItem> getAllInventory() throws ExecutionException, InterruptedException {
        return firestore.collection("inventory").get().get().getDocuments().stream()
                .map(doc -> doc.toObject(InventoryItem.class))
                .collect(Collectors.toList());
    }

    public List<User> getAllVolunteers() throws ExecutionException, InterruptedException {
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

    public List<ReliefRequest> getVolunteerTaskHistory(String volunteerId)
            throws ExecutionException, InterruptedException {
        return firestore.collection("requests")
                .whereEqualTo("assignedVolunteerId", volunteerId)
                .whereEqualTo("status", "Completed")
                .get().get().getDocuments().stream()
                .map(doc -> doc.toObject(ReliefRequest.class))
                .collect(Collectors.toList());
    }
}