package com.example.teamder.model;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class Group {
    private String uid;
    private ArrayList<String> userIds;
    private String courseId;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ArrayList<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(ArrayList<String> userIds) {
        this.userIds = userIds;
    }

    public String getCourseIDs() {
        return courseId;
    }

    public void setCourseIDs(String courseIDs) {
        this.courseId = courseIDs;
    }

    public static Group parseGroup(DocumentSnapshot document) {
        Group group = new Group();
        group.setUid(document.getId());
        group.setUserIds((ArrayList<String>) document.get("userIds"));
        group.setCourseIDs(document.getString("courseIDs"));
        return group;
    }
}
