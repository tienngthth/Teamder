package com.example.teamder.model;

import java.util.ArrayList;

public class User {
    private String name;
    private UserType type;
    private String uid;
    private ArrayList<String> coursesIDs;
    private ArrayList<String> groupIDs;

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
}
