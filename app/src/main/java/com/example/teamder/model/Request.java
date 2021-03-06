package com.example.teamder.model;

import static com.example.teamder.activity.RequestActivity.Status.pending;
import static com.example.teamder.util.DateTimeUtil.getCurrentTime;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class Request {

    private ArrayList<String> parties = new ArrayList<>();
    private String requesterID;
    private String requesteeID;
    private String courseName;
    private String timeStamp = getCurrentTime();
    private String message = "";
    private String id;
    private String status = pending.toString();

    public Request(String courseName, String requesterID, String message, String requesteeID) {
        this.courseName = courseName;
        this.requesterID = requesterID;
        this.message = message;
        this.requesteeID = requesteeID;
        parties.add(requesterID);
        parties.add(requesteeID);
    }

    public Request() {
    }

    public static Request parseRequest(DocumentSnapshot document) {
        Request request = new Request();
        request.setRequesteeID(document.getString("requesteeID"));
        request.setRequesterID(document.getString("requesterID"));
        request.setMessage(document.getString("message"));
        request.setTimeStamp(document.getString("timeStamp"));
        request.setStatus(document.getString("status"));
        request.setCourseName(document.getString("courseName"));
        request.setId(document.getId());
        ArrayList<String> parties = (ArrayList<String>) document.getData().get("parties");
        request.setParties(parties == null ? new ArrayList<>() : parties);
        return request;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequesteeID() {
        return requesteeID;
    }

    public void setRequesteeID(String requesteeID) {
        this.requesteeID = requesteeID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequesterID() {
        return requesterID;
    }

    public void setRequesterID(String requesterID) {
        this.requesterID = requesterID;
    }

    public ArrayList<String> getParties() {
        return parties;
    }

    public void setParties(ArrayList<String> parties) {
        this.parties = parties;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
