package com.scd_project.disaster_relief_portal_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItem {
    private String itemId;
    private String name;
    private String category; // e.g., "Food", "Medicine", "Water"
    private int quantity;
    private String unit; // e.g., "kg", "liters", "boxes"
    private String location; // Warehouse location
}