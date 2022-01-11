package com.example.teamder.repository;

import com.example.teamder.model.Message;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;


public class MessageRepository {

    public static void createMessage(Message message) {
        FirebaseFirestore
                .getInstance()
                .collection("messages")
                .add(message);
    }

    public static ListenerRegistration addSnapshotListenerForMessageByGroupId(String groupId, CallbackInterfaces.DocumentSnapshotsCallBack documentSnapshotsCallBack) {
        return FirebaseFirestore.getInstance()
                .collection("messages")
                .whereEqualTo("groupId", groupId)
                .orderBy("timeStamp")
                .addSnapshotListener((value, error) -> {
                    assert value != null;
                    documentSnapshotsCallBack.onCallBack(value.getDocuments());
                });
    }

}
