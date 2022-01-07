package com.example.teamder.repository;

import com.example.teamder.model.Request;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class RequestRepository {

    public static void createRequest(Request request) {
        FirebaseFirestore.getInstance().collection("requests").add(request);
    }

    public static void getPendingRequestByParties(ArrayList<String> parties, CallbackInterfaces.QuerySnapShotCallBack querySnapShotCallBack) {
        FirebaseFirestore.getInstance()
                .collection("requests")
                .whereEqualTo("status", "Pending")
                .whereIn("parties", Arrays.asList(parties, new ArrayList<String>(){{ add(parties.get(1));  add(parties.get(0)); }}))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        querySnapShotCallBack.onCallBack(Objects.requireNonNull(task.getResult()));
                    }
                });
    }

    public static void getApprovedRequestByParties(ArrayList<String> parties, CallbackInterfaces.QuerySnapShotCallBack querySnapShotCallBack) {
        FirebaseFirestore.getInstance()
                .collection("requests")
                .whereEqualTo("status", "approved")
                .whereIn("parties", Arrays.asList(parties, new ArrayList<String>(){{ add(parties.get(1));  add(parties.get(0)); }}))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        querySnapShotCallBack.onCallBack(Objects.requireNonNull(task.getResult()));
                    }
                });
    }

    public static void getPendingRequestOfCourseByParties(ArrayList<String> parties, String course, CallbackInterfaces.QuerySnapShotCallBack querySnapShotCallBack) {
        FirebaseFirestore.getInstance()
                .collection("requests")
                .whereEqualTo("status", "Pending")
                .whereEqualTo("courseName", course)
                .whereIn("parties", Arrays.asList(parties, new ArrayList<String>(){{ add(parties.get(1));  add(parties.get(0)); }}))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        querySnapShotCallBack.onCallBack(Objects.requireNonNull(task.getResult()));
                    }
                });
    }

    public static void getPendingRequestByFieldValue(String field, String value, CallbackInterfaces.QuerySnapShotCallBack querySnapShotCallBack) {
        FirebaseFirestore.getInstance()
                .collection("requests")
                .whereEqualTo(field, value)
                .whereEqualTo("status", "Pending")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        querySnapShotCallBack.onCallBack(Objects.requireNonNull(task.getResult()));
                    }
                });
    }

    public static void getApproveRequestByCourseName(String courseName, CallbackInterfaces.QuerySnapShotCallBack querySnapShotCallBack) {
        FirebaseFirestore.getInstance()
                .collection("requests")
                .whereEqualTo("courseName", courseName)
                .whereEqualTo("status", "approved")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        querySnapShotCallBack.onCallBack(Objects.requireNonNull(task.getResult()));
                    }
                });
    }
}
