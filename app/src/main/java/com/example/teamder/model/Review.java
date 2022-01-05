package com.example.teamder.model;

import com.google.firebase.firestore.DocumentSnapshot;

public class Review {
    private String uid;
    private String userID;
    private String comment;
    private String timeStamp;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public static Review parseReview(DocumentSnapshot document) {
        Review review = new Review();
        review.setComment(document.getString("comment"));
        review.setTimeStamp(document.getString("timeStamp"));
        return review;
    }
}
