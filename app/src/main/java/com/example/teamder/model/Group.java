package com.example.teamder.model;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class Group {

    private String id;
    private ArrayList<String> userIds;
    private String courseName;
    private Boolean isActive = true;

    public Group(ArrayList<String> userIds, String courseName) {
        this.userIds = userIds;
        this.courseName = courseName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(ArrayList<String> userIds) {
        this.userIds = userIds;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Group() {
    }

    public static Group parseGroup(DocumentSnapshot document) {
        Group group = new Group();
        group.setId(document.getId());
        ArrayList<String> userIds = (ArrayList<String>) document.getData().get("userIds");
        group.setUserIds(userIds);
        group.setCourseName(document.getString("courseName"));
        group.setIsActive(document.getBoolean("isActive"));
        return group;
    }
}
