package com.scd_project.disaster_relief_portal_backend.controller;

import com.scd_project.disaster_relief_portal_backend.model.User;
import com.scd_project.disaster_relief_portal_backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint to save the user profile and assign their role.
     * In an end-to-end flow, the frontend calls this immediately after
     * Firebase Auth signup is successful.
     */
    @PostMapping("/profile")
    public String saveProfile(@RequestBody User user) {
        try {
            // Automatically get the UID from the validated Firebase token
            String uid = (String) org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();

            user.setUid(uid); // Override whatever the user sent in the body
            return authService.registerUser(user);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * A helper endpoint to verify if the token authentication is working.
     */
    @GetMapping("/me")
    public String checkAuth() {
        return "You are authenticated! Your UID is: " +
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication()
                        .getPrincipal();
    }
}