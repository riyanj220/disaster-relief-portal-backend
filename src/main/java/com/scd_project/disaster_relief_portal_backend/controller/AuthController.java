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

    @PostMapping("/profile")
    public String saveProfile(@RequestBody User user) {
        try {
            return authService.saveUserProfile(user);
        } catch (Exception e) {
            return "Error saving profile: " + e.getMessage();
        }
    }
}