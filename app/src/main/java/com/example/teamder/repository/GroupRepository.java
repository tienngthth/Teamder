package com.example.teamder.repository;

import com.example.teamder.model.Group;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

public class GroupRepository {

    public static void createGroup(Group group, CallbackInterfaces.DocRefCallBack docRefCallBack, CallbackInterfaces.EmptyCallBack emptyCallBack) {
        FirebaseFirestore
                .getInstance()
                .collection("groups")
                .add(group)
                .addOnSuccessListener(docRefCallBack::onCallBack)
                .addOnFailureListener(e -> emptyCallBack.onCallBack());
    }

    public static void getGroupById(String groupId, CallbackInterfaces.DocumentSnapshotCallBack documentSnapshotCallBack) {
        FirebaseFirestore
                .getInstance()
                .collection("groups")
                .document(groupId)
                .get()
                .addOnSuccessListener(documentSnapshotCallBack::onCallBack);
    }

    public static void getGroupByCourseNameByUserIds(String courseName, List userIds, CallbackInterfaces.QuerySnapShotCallBack querySnapShotCallBack) {
        FirebaseFirestore.getInstance()
                .collection("groups")
                .whereArrayContainsAny("userIds", userIds)
                .whereEqualTo("courseName", courseName)
                .whereNotEqualTo("isActive", false)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        querySnapShotCallBack.onCallBack(Objects.requireNonNull(task.getResult()));
                    }
                });
    }

    public static void getAllActiveGroupOfUser(String userId, CallbackInterfaces.QuerySnapShotCallBack querySnapShotCallBack) {
        FirebaseFirestore.getInstance()
                .collection("groups")
                .whereArrayContains("userIds", userId)
                .whereNotEqualTo("isActive", false)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        querySnapShotCallBack.onCallBack(Objects.requireNonNull(task.getResult()));
                    }
                });
    }

}
