package com.scd_project.disaster_relief_portal_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReliefRequest {
    private String requestId; // Unique ID for the request
    private String citizenId; // Link to User.uid (Foreign Key concept)
    private String assistanceType; // e.g., "Food", "Medical", "Shelter"
    private String urgency; // "High", "Medium", "Low"
    private int familySize;
    private String address;
    private String status; // "Pending", "Approved", "Rejected", "Completed"
    private String assignedVolunteerId; // Initially null
    private double hoursSpent;
    private Long timestamp; // For sorting history
}