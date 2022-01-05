package com.example.teamder.model;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

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
        user.setName(document.getString("name"));
        user.setMajor(document.getString("major"));
        user.setGPA(Double.parseDouble(Objects.requireNonNull(document.getString("GPA"))));
        user.setIntroduction(document.getString("introduction"));
        user.setPhone(document.getString("phone"));
        user.setsId(document.getString("sID"));
        user.setUid(document.getString("uid"));
        user.setId(document.getId());
        ArrayList<String> courses = (ArrayList<String>) document.getData().get("courses");
        user.setCourses(courses == null ? new ArrayList<>() : courses);
        setUser(user);
    }

}
