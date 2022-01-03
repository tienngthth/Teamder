package com.example.teamder.model;

import java.util.ArrayList;

public class Group {
    private String uid;
    private ArrayList<String> userIDs;
    private String leaderIDs;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ArrayList<String> getUserIDs() {
        return userIDs;
    }

    public void setUserIDs(ArrayList<String> userIDs) {
        this.userIDs = userIDs;
    }

    public String getLeaderIDs() {
        return leaderIDs;
    }

    public void setLeaderIDs(String leaderIDs) {
        this.leaderIDs = leaderIDs;
    }
}
