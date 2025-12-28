package com.scd_project.disaster_relief_portal_backend.controller;

import com.scd_project.disaster_relief_portal_backend.model.User;
import com.scd_project.disaster_relief_portal_backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Used during Signup to create the profile
    @PostMapping("/profile")
    public String saveProfile(@RequestBody User user) {
        try {
            // Extracts UID from the verified JWT token
            String uid = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            user.setUid(uid);
            return authService.registerUser(user);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // Used by React (Zustand) to fetch the logged-in user's details and role
    @GetMapping("/me")
    public User getCurrentUser() {
        try {
            // Get UID from the Spring Security context
            String uid = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            // Return the full User object from Firestore
            return authService.getUserByUid(uid);
        } catch (Exception e) {
            // Returning null or a 401/404 is cleaner than a stack trace for the frontend
            return null;
        }
    }
}