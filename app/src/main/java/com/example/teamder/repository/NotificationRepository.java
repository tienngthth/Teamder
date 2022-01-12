package com.example.teamder.repository;

import com.example.teamder.model.Notification;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class NotificationRepository {

    public static void getNotificationByUserIdAndSeenValue(String userId, boolean isSeen, CallbackInterfaces.QuerySnapShotCallBack querySnapShotCallBack) {
        FirebaseFirestore.getInstance()
                .collection("notifications")
                .whereEqualTo("userId", userId)
                .whereEqualTo("seen", isSeen)
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        querySnapShotCallBack.onCallBack(Objects.requireNonNull(task.getResult()));
                    }
                });
    }

    public static ListenerRegistration getNotificationListenerByUserIdAndSeenValue(String userId, boolean isSeen, CallbackInterfaces.QuerySnapShotCallBack querySnapShotCallBack) {
        return FirebaseFirestore.getInstance()
                .collection("notifications")
                .whereEqualTo("userId", userId)
                .whereEqualTo("seen", isSeen)
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, error) -> {
                    assert snapshot != null;
                    querySnapShotCallBack.onCallBack(snapshot);
                });
    }

    public static void getNotificationByUserIdAndMessage(String userId, String message, CallbackInterfaces.QuerySnapShotCallBack querySnapShotCallBack) {
        FirebaseFirestore.getInstance()
                .collection("notifications")
                .whereEqualTo("userId", userId)
                .whereEqualTo("message", message)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        querySnapShotCallBack.onCallBack(Objects.requireNonNull(task.getResult()));
                    }
                });
    }

    public static ListenerRegistration addSnapshotListenerForNotificationByUserIdAndHasPushedValue(String userId, boolean hashPushed, CallbackInterfaces.ListDocumentChangeCallBack listDocumentChangeCallBack) {
        return FirebaseFirestore.getInstance()
                .collection("notifications")
                .whereEqualTo("userId", userId)
                .whereEqualTo("hasPushed", hashPushed)
                .addSnapshotListener((value, error) -> {
                    assert value != null;
                    listDocumentChangeCallBack.onCallBack(Objects.requireNonNull(value.getDocumentChanges()));
                });
    }

    public static void createNotification(Notification notification) {
        FirebaseFirestore.getInstance().collection("notifications").add(notification);
    }
}
