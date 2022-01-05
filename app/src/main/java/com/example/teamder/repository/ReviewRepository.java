package com.example.teamder.repository;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ReviewRepository {

    public static void getReviewByUserId(String userId, CallbackInterfaces.QuerySnapShotCallBack querySnapShotCallBack) {
        FirebaseFirestore.getInstance()
                .collection("reviews")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        querySnapShotCallBack.onCallBack(Objects.requireNonNull(task.getResult()));
                    }
                });
    }
}
