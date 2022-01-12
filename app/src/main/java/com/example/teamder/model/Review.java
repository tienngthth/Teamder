package com.example.teamder.model;

import static com.example.teamder.util.DateTimeUtil.getCurrentTime;

import com.google.firebase.firestore.DocumentSnapshot;

public class Review {
    private String userID;
    private String comment;
    private String timeStamp = getCurrentTime();

    public Review(String userID, String comment) {
        this.userID = userID;
        this.comment = comment;
    }

    public Review() {
    }

    public static Review parseReview(DocumentSnapshot document) {
        Review review = new Review();
        review.setComment(document.getString("comment"));
        review.setTimeStamp(document.getString("timeStamp"));
        return review;
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
}
