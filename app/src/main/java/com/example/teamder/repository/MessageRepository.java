package com.example.teamder.repository;

import com.example.teamder.model.Message;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;


public class MessageRepository {

    public static void createMessage(Message message, CallbackInterfaces.DocRefCallBack docRefCallBack) {
        FirebaseFirestore
                .getInstance()
                .collection("messages")
                .add(message).addOnSuccessListener(task -> docRefCallBack.onCallBack(task));
    }

    public static void getMessageByGroupId(String groupId, CallbackInterfaces.QuerySnapShotCallBack querySnapShotCallBack) {
        FirebaseFirestore
                .getInstance()
                .collection("messages")
                .whereEqualTo("groupId", groupId).orderBy("timeStamp")
                .get()
                .addOnSuccessListener(querySnapShotCallBack::onCallBack);
    }

}
