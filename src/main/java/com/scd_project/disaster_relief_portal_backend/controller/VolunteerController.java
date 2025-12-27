package com.scd_project.disaster_relief_portal_backend.controller;

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
}