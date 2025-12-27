package com.scd_project.disaster_relief_portal_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String uid;
    private String email;
    private String firstName;
    private String lastName;
    private String role; // "CITIZEN", "VOLUNTEER", or "ADMIN"

    // For Volunteers specifically
    private String status; // "ON_DUTY" or "OFF_DUTY"
    private String primarySkill;
}