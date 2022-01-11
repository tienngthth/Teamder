package com.example.teamder.repository;

import android.util.Log;

import com.example.teamder.model.Group;
import com.google.firebase.firestore.FirebaseFirestore;
import static com.example.teamder.repository.UtilRepository.updateFieldToDb;

import java.util.List;
import java.util.Objects;

public class GroupRepository {
    private static String TAG = "Group Repository";

    public static void createGroup(Group group, CallbackInterfaces.EmptyCallBack emptyCallBack) {
        FirebaseFirestore.getInstance()
                .collection("groups")
                .add(group)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        emptyCallBack.onCallBack();
                    }
                    else {
                        Log.d(TAG, String.valueOf(task.getException()));
                    }
        });
    }

    public static void getGroup(String courseId, List userIds, CallbackInterfaces.QuerySnapShotCallBack querySnapShotCallBack) {
        FirebaseFirestore.getInstance()
                .collection("groups")
                .whereArrayContainsAny("userIds", userIds)
                .whereEqualTo("courseIDs", courseId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        querySnapShotCallBack.onCallBack(Objects.requireNonNull(task.getResult()));
                    }
                });
    }

    public static void getAllGroup( String userId, CallbackInterfaces.QuerySnapShotCallBack querySnapShotCallBack) {
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

    public static void deactivateGroup(String groupId) {
        updateFieldToDb("groups", groupId, "isActive", false);
    }

}
