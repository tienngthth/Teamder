package com.example.teamder.model;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ToVisitUserList {

    private volatile static ToVisitUserList toVisitUserList;

    private final ArrayList<String> userIDs = new ArrayList<>();

    private ToVisitUserList() {
    }

    public static ToVisitUserList getInstance() {
        if (toVisitUserList == null) {
            synchronized (ToVisitUserList.class) {
                if (toVisitUserList == null) {
                    toVisitUserList = new ToVisitUserList();
                }
            }
        }
        return toVisitUserList;
    }

    public void removeAllUserIDs() {
        userIDs.removeAll(userIDs);
    }

    public void addUserID(String userID) {
        userIDs.add(userID);
    }

    public String getUserID(int index) {
        return userIDs.get(index);
    }

    public String getUserID() {
        return getUserID(0);
    }

    public void removeUserID() {
        userIDs.remove(0);
    }

    public ArrayList<String> getUserIDs() {
        return userIDs;
    }

    public void generateUsersList(QuerySnapshot querySnapshot) {
        this.removeAllUserIDs();
        for (QueryDocumentSnapshot document : querySnapshot) {
            this.addUserID(document.getId());
        }
    }

}
