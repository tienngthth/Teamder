package com.example.teamder.model;

import java.util.ArrayList;

public class User {
    private String name;

    private String email;
    private UserType type;
    private String uid;
    private ArrayList<String> coursesIDs;
    private ArrayList<String> groupIDs;
    private String id;

    public ArrayList<String> getCoursesIDs() {
        return coursesIDs;
    }

    public void setCoursesIDs(ArrayList<String> coursesIDs) {
        this.coursesIDs = coursesIDs;
    }

    public ArrayList<String> getGroupIDs() {
        return groupIDs;
    }

    public void setGroupIDs(ArrayList<String> groupIDs) {
        this.groupIDs = groupIDs;
    }

    public User(String name, String email, String uid) {
        this.name = name;
        this.email = email;
        this.uid = uid;
        type = UserType.NORMAL;
    }

    public User() {
        type = UserType.NORMAL;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(Boolean isLeader, Boolean isSuperUser) {
        if (isLeader) {
            this.type = UserType.LEADER;
        }
        if (isSuperUser) {
            this.type = UserType.SUPER_USER;
        }
    }

    public String getName() {
        return name;
    }

    public UserType getType() {
        return type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
