package com.scd_project.disaster_relief_portal_backend.service;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.scd_project.disaster_relief_portal_backend.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final Firestore firestore;

    public String registerUser(User user) throws Exception {
        // 1. Attach Role to Firebase Auth as a Custom Claim
        // This makes the role part of the cryptographically signed JWT token
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        FirebaseAuth.getInstance().setCustomUserClaims(user.getUid(), claims);

        // 2. Save the full User Profile to Firestore for database queries
        WriteResult result = firestore.collection("users")
                .document(user.getUid())
                .set(user)
                .get();

        return "User profile saved and role '" + user.getRole() + "' assigned at: " + result.getUpdateTime();
    }
}