package com.example.teamder.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class AuthenticationRepository {

    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    public static void signInWithEmailAndPassword(String email, String password, CallbackInterfaces.EmptyCallBack successCallBack, CallbackInterfaces.EmptyCallBack failCallBack) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        successCallBack.onCallBack();
                    } else {
                        failCallBack.onCallBack();
                    }
                });
    }

    public static void createUserWithEmailAndPassword(String email, String password, CallbackInterfaces.FirebaseUserCallBack successCallBack, CallbackInterfaces.EmptyCallBack failCallBack) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        successCallBack.onCallBack(Objects.requireNonNull(task.getResult()).getUser());
                    } else {
                        failCallBack.onCallBack();
                    }
                });
    }

}
