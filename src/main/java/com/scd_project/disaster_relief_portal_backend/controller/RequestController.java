package com.scd_project.disaster_relief_portal_backend.controller;

import com.scd_project.disaster_relief_portal_backend.model.ReliefRequest;
import com.scd_project.disaster_relief_portal_backend.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public String createRequest(@RequestBody ReliefRequest reliefRequest) {
        try {
            // Get UID from the authenticated Firebase Token (your Auth module)
            String uid = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            reliefRequest.setCitizenId(uid);

            return "Request created with ID: " + requestService.submitRequest(reliefRequest);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/history")
    public List<ReliefRequest> getMyHistory() {
        try {
            // 1. Get the UID from the validated Token (the "Passport")
            String currentUid = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            // 2. Pass that UID to the service to fetch only their records
            return requestService.getCitizenHistory(currentUid);
        } catch (Exception e) {
            // In a real app, you'd use a Global Exception Handler,
            // but for now, we return an empty list or throw an error.
            throw new RuntimeException("Could not fetch history: " + e.getMessage());
        }
    }
}