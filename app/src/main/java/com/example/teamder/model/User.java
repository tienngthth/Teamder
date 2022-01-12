package com.example.teamder.model;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class User {

    private String id;
    private String name;
    private String uid;
    private String email;
    private String gpa = "4";
    private String major = "";
    private String sId = "";
    private String phone = "";
    private String introduction = "";
    private ArrayList<String> courses = new ArrayList<>();
    private ArrayList<String> groupIDs = new ArrayList<>();
    private ArrayList<String> visitedTeameeIDs = new ArrayList<>();

    public User(String name, String email, String uid) {
        this.name = name;
        this.uid = uid;
        this.email = email;
    }

    public User() {
    }

    public static User parseUser(QueryDocumentSnapshot document) {
        User user = new User();
        user.setName(document.getString("name"));
        user.setMajor(document.getString("major"));
        user.setGpa(document.getString("gpa"));
        user.setIntroduction(document.getString("introduction"));
        user.setPhone(document.getString("phone"));
        user.setsId(document.getString("sId"));
        user.setUid(document.getString("uid"));
        user.setEmail(document.getString("email"));
        user.setId(document.getId());
        ArrayList<String> courses = (ArrayList<String>) document.getData().get("courses");
        user.setCourses(courses == null ? new ArrayList<>() : courses);
        ArrayList<String> visitedTeameeIDs = (ArrayList<String>) document.getData().get("visitedTeameeIDs");
        user.setVisitedTeameeIDs(visitedTeameeIDs == null ? new ArrayList<>() : visitedTeameeIDs);
        return user;
    }

    public static User parseUser(DocumentSnapshot document) {
        User user = new User();
        user.setName(document.getString("name"));
        user.setMajor(document.getString("major"));
        user.setGpa(document.getString("gpa"));
        user.setIntroduction(document.getString("introduction"));
        user.setPhone(document.getString("phone"));
        user.setsId(document.getString("sId"));
        user.setUid(document.getString("uid"));
        user.setEmail(document.getString("email"));
        user.setId(document.getId());
        ArrayList<String> courses = (ArrayList<String>) document.getData().get("courses");
        user.setCourses(courses == null ? new ArrayList<>() : courses);
        ArrayList<String> visitedTeameeIDs = (ArrayList<String>) document.getData().get("visitedTeameeIDs");
        user.setVisitedTeameeIDs(visitedTeameeIDs == null ? new ArrayList<>() : visitedTeameeIDs);
        return user;
    }

    public void addCourse(String course) {
        courses.add(course);
    }

    public void removeCourse(int index) {
        courses.remove(index);
    }

    public ArrayList<String> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<String> coursesIDs) {
        this.courses = coursesIDs;
    }

    public ArrayList<String> getGroupIDs() {
        return groupIDs;
    }

    public void setGroupIDs(ArrayList<String> groupIDs) {
        this.groupIDs = groupIDs;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGpa() {
        return gpa;
    }

    public void setGpa(String gpa) {
        this.gpa = gpa;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getVisitedTeameeIDs() {
        return visitedTeameeIDs;
    }

    public void setVisitedTeameeIDs(ArrayList<String> visitedTeameeIDs) {
        this.visitedTeameeIDs = visitedTeameeIDs;
    }

    public void addVisitedTeameeIDs(String userID) {
        visitedTeameeIDs.add(userID);
    }
}
