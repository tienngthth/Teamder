package com.example.teamder.model;

import static com.example.teamder.model.User.parseUser;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class CurrentUser {

    private static CurrentUser instance;

    public User user;

    private CurrentUser() {
    }

    public static CurrentUser getInstance() {
        if (instance == null) {
            instance = new CurrentUser();
        }
        return instance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void updateUser(QueryDocumentSnapshot document) {
        setUser(parseUser(document));
    }

    public void updateUser(DocumentSnapshot document) {
        setUser(parseUser(document));
    }

}
