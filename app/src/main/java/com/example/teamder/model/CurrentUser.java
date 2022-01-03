package com.example.teamder.model;

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
        User user = new User();
        user.setEmail(document.getString("email"));
        user.setType(document.getString("type"));
        user.setName(document.getString("name"));
        user.setUid(document.getString("uid"));
        user.setId(document.getId());
        setUser(user);
    }

}
