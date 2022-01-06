package com.example.teamder.repository;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UtilRepository {

    public static void updateFieldToDb(String collection, String id, String field, Boolean value) {
        FirebaseFirestore
                .getInstance()
                .collection(collection)
                .document(id)
                .update(field, value);
    }

    public static void updateFieldToDb(String collection, String id, String field, String value, CallbackInterfaces.VoidCallBack voidCallBack) {
        FirebaseFirestore
                .getInstance()
                .collection(collection)
                .document(id)
                .update(field, value)
                .addOnSuccessListener(voidCallBack::onCallBack);
    }

    public static void updateFieldToDb(String collection, String id, String field, String value) {
        FirebaseFirestore
                .getInstance()
                .collection(collection)
                .document(id)
                .update(field, value);
    }

    public static void updateFieldToDb(String collection, String id, String field, Long value) {
        FirebaseFirestore.getInstance().collection(collection).document(id).update(field, value);
    }

    public static void updateFieldToDb(String collection, String id, String field, ArrayList<String> value) {
        FirebaseFirestore.getInstance().collection(collection).document(id).update(field, value);
    }

}
