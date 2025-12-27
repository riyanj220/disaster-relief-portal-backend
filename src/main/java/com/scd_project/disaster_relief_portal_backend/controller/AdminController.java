package com.scd_project.disaster_relief_portal_backend.controller;

import com.scd_project.disaster_relief_portal_backend.model.InventoryItem;
import com.scd_project.disaster_relief_portal_backend.model.ReliefRequest;
import com.scd_project.disaster_relief_portal_backend.model.User;
import com.scd_project.disaster_relief_portal_backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // View all requests in the system
    @GetMapping("/requests")
    public List<ReliefRequest> getAllRequests() throws Exception {
        return adminService.getAllRequests();
    }

    // Approve/Reject or Assign Volunteer to a Request
    @PatchMapping("/requests/{id}")
    public String updateRequest(@PathVariable String id, @RequestBody Map<String, Object> updates) throws Exception {
        return adminService.updateRequestStatus(id, updates);
    }

    // Add new inventory
    @PostMapping("/inventory")
    public String addItem(@RequestBody InventoryItem item) throws Exception {
        return "Item added with ID: " + adminService.addInventoryItem(item);
    }

    // View full inventory
    @GetMapping("/inventory")
    public List<InventoryItem> getInventory() throws Exception {
        return adminService.getAllInventory();
    }

    // Get all registered volunteers
    @GetMapping("/volunteers")
    public List<User> getVolunteers() throws Exception {
        return adminService.getAllVolunteers();
    }

    // Remove a volunteer
    @DeleteMapping("/volunteers/{id}")
    public String deleteVolunteer(@PathVariable String id) {
        return adminService.removeVolunteer(id);
    }
}