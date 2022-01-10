package com.example.teamder.repository;

import com.example.teamder.model.Review;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ReviewRepository {

    public static void createReview(Review review) {
        FirebaseFirestore.getInstance().collection("reviews").add(review);
    }

    public static void getReviewByUserId(String userId, CallbackInterfaces.QuerySnapShotCallBack querySnapShotCallBack) {
        FirebaseFirestore.getInstance()
                .collection("reviews")
                .whereEqualTo("userID", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        querySnapShotCallBack.onCallBack(Objects.requireNonNull(task.getResult()));
                    }
                });
    }
}
