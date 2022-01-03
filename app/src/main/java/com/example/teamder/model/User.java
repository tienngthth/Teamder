package com.example.teamder.model;

import com.google.firebase.firestore.DocumentSnapshot;

public class User {

    private String name;
    private String email;
    private String uid;
    private String id;

    public User() {

    }

    public User(String name, String email, String uid) {
        this.name = name;
        this.email = email;
        this.uid = uid;
    }

    public static User parseUser(DocumentSnapshot document) {
        User volunteer = new User();
        volunteer.setName(document.getString("name"));
        volunteer.setEmail(document.getString("email"));
        return volunteer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
