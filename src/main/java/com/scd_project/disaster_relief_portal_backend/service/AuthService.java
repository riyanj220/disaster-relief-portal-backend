package com.scd_project.disaster_relief_portal_backend.service;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.scd_project.disaster_relief_portal_backend.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final Firestore firestore;

    public String saveUserProfile(User user) throws ExecutionException, InterruptedException {
        // Saves the user object to a collection named "users" using their UID as the
        // document ID
        // .set() returns an ApiFuture, .get() waits for the result
        WriteResult result = firestore.collection("users").document(user.getUid()).set(user).get();
        return result.getUpdateTime().toString();
    }
}