package com.scd_project.disaster_relief_portal_backend.controller;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.scd_project.disaster_relief_portal_backend.model.ReliefRequest;
import com.scd_project.disaster_relief_portal_backend.service.VolunteerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/volunteer")
@RequiredArgsConstructor
public class VolunteerController {

    private final Firestore firestore;
    private final VolunteerService volunteerService;

    // Update Availability
    @PatchMapping("/status")
    public String updateAvailability(@RequestBody Map<String, String> statusMap) {
        try {
            String uid = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String newStatus = statusMap.get("status");
            return volunteerService.updateStatus(uid, newStatus);
        } catch (Exception e) {
            return "Error updating status: " + e.getMessage();
        }
    }

    // Get Assigned Tasks
    @GetMapping("/tasks")
    public List<ReliefRequest> getMyTasks() {
        try {
            String uid = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return volunteerService.getAssignedTasks(uid);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching tasks: " + e.getMessage());
        }
    }

    // controller/VolunteerController.java
    @GetMapping("/stats")
    public Map<String, Object> getDashboardStats() throws Exception {
        String uid = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return volunteerService.getVolunteerStats(uid);
    }

    @PatchMapping("/tasks/{id}")
    public String completeTask(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        try {
            // Now extracting both status and hours from the payload
            String newStatus = (String) payload.get("status");
            Double hours = Double.parseDouble(payload.get("hours").toString());

            DocumentReference requestRef = firestore.collection("requests").document(id);
            requestRef.update("status", newStatus, "hoursSpent", hours).get();
            return "Task updated";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

}