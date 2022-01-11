package com.example.teamder.repository;

import static com.example.teamder.activity.RequestActivity.Status.pending;

import com.example.teamder.model.Request;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class RequestRepository {

    public static void getRequestById(String id, CallbackInterfaces.DocumentSnapshotCallBack documentSnapshotCallBack) {
        FirebaseFirestore.getInstance()
                .collection("requests")
                .document(id)
                .get()
                .addOnSuccessListener(documentSnapshotCallBack::onCallBack);
    }

    public static void getRequestByUserIdStatusAndCourseName(String userId, String status, String courseName, CallbackInterfaces.QuerySnapShotCallBack querySnapShotCallBack) {
        FirebaseFirestore.getInstance()
                .collection("requests")
                .whereArrayContains("parties", userId)
                .whereEqualTo("status", status)
                .whereEqualTo("courseName", courseName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        querySnapShotCallBack.onCallBack(Objects.requireNonNull(task.getResult()));
                    }
                });
    }

    public static void createRequest(Request request, CallbackInterfaces.DocRefCallBack docRefCallBack) {
        FirebaseFirestore
                .getInstance()
                .collection("requests")
                .add(request)
                .addOnSuccessListener(documentReference -> {
                    docRefCallBack.onCallBack(documentReference);
                });
    }

    public static void getRequestsByPartiesAndStatus(String status, ArrayList<String> parties, CallbackInterfaces.QuerySnapShotCallBack querySnapShotCallBack) {
        FirebaseFirestore.getInstance()
                .collection("requests")
                .whereEqualTo("status", status)
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
                .whereEqualTo("status", pending.toString())
                .whereEqualTo("courseName", course)
                .whereIn("parties", Arrays.asList(parties, new ArrayList<String>(){{ add(parties.get(1));  add(parties.get(0)); }}))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        querySnapShotCallBack.onCallBack(Objects.requireNonNull(task.getResult()));
                    }
                });
    }

    public static void getRequestByStatusAndFieldValue(String status, String field, String value, CallbackInterfaces.QuerySnapShotCallBack querySnapShotCallBack) {
        FirebaseFirestore.getInstance()
                .collection("requests")
                .whereEqualTo(field, value)
                .whereEqualTo("status", status)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        querySnapShotCallBack.onCallBack(Objects.requireNonNull(task.getResult()));
                    }
                });
    }

}
