package com.example.teamder.repository;

import android.util.Log;

import com.example.teamder.model.CurrentUser;
import com.example.teamder.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;


public class UserRepository {

    private static final String TAG = UserRepository.class.getName();

    public static void getUserByFieldValue(String field, String value, CallbackInterfaces.QuerySnapShotCallBack querySnapShotCallBack) {
        FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo(field, value)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        querySnapShotCallBack.onCallBack(Objects.requireNonNull(task.getResult()));
                    } else {
                        Log.w(TAG, "Error getting user with id " + value, task.getException());
                    }
                });
    }

    public static void getOtherUserByFieldValue(String field, String value, CallbackInterfaces.QuerySnapShotCallBack querySnapShotCallBack) {
        String currentUserId = CurrentUser.getInstance().getUser().getId();
        FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo(field, value)
                .whereNotEqualTo("id", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        querySnapShotCallBack.onCallBack(Objects.requireNonNull(task.getResult()));
                    } else {
                        Log.w(TAG, "Error getting user with id " + value, task.getException());
                    }
                });
    }

    public static void getUserById(String userID, CallbackInterfaces.DocumentSnapshotCallBack documentSnapshotCallBack) {
        FirebaseFirestore.getInstance().collection("users")
                .document(userID)
                .get()
                .addOnSuccessListener(documentSnapshotCallBack::onCallBack);
    }

    public static void addUser(User user, CallbackInterfaces.DocRefCallBack docRefCallBack, CallbackInterfaces.EmptyCallBack emptyCallBack) {
        FirebaseFirestore.getInstance().collection("users")
                .add(user)
                .addOnSuccessListener(docRefCallBack::onCallBack)
                .addOnFailureListener(e -> emptyCallBack.onCallBack());
    }

    public static void getUsersByCourse(ArrayList<String> courses, String uid, CallbackInterfaces.QuerySnapShotCallBack querySnapShotCallBack) {
        FirebaseFirestore.getInstance().collection("users")
//                .whereNotEqualTo("uid", uid)
                .whereArrayContainsAny("courses", courses)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        querySnapShotCallBack.onCallBack(Objects.requireNonNull(task.getResult()));
                    }
                });
    }

}
