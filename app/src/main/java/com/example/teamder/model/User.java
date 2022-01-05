package com.example.teamder.model;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class User {

    private String id;
    private String name;
    private String uid;
    private double GPA = 4;
    private String major = "";
    private String sId = "";
    private String phone = "";
    private String introduction = "";
    private ArrayList<String> courses = new ArrayList<>();
    private ArrayList<String> groupIDs = new ArrayList<>();
    private ArrayList<String> visitedTeameeIDs = new ArrayList<>();

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

    public User(String name, String email, String uid) {
        this.name = name;
        this.uid = uid;
    }

    public User() {}

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public double getGPA() {
        return GPA;
    }

    public void setGPA(double GPA) {
        this.GPA = GPA;
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

    public void addVisitedTeameeIDs(String userID) {
        visitedTeameeIDs.add(userID);
    }

    public void setVisitedTeameeIDs(ArrayList<String> visitedTeameeIDs) {
        this.visitedTeameeIDs = visitedTeameeIDs;
    }

    public static User parseUser(QueryDocumentSnapshot document) {
        User user = new User();
        user.setName(document.getString("name"));
        user.setMajor(document.getString("major"));
        user.setGPA(Double.parseDouble(Objects.requireNonNull(document.getString("GPA"))));
        user.setIntroduction(document.getString("introduction"));
        user.setPhone(document.getString("phone"));
        user.setsId(document.getString("sID"));
        user.setUid(document.getString("uid"));
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
        user.setGPA(Double.parseDouble(Objects.requireNonNull(document.getString("GPA"))));
        user.setIntroduction(document.getString("introduction"));
        user.setPhone(document.getString("phone"));
        user.setsId(document.getString("sID"));
        user.setUid(document.getString("uid"));
        user.setId(document.getId());
        ArrayList<String> courses = (ArrayList<String>) document.getData().get("courses");
        user.setCourses(courses == null ? new ArrayList<>() : courses);
        ArrayList<String> visitedTeameeIDs = (ArrayList<String>) document.getData().get("visitedTeameeIDs");
        user.setVisitedTeameeIDs(visitedTeameeIDs == null ? new ArrayList<>() : visitedTeameeIDs);
        return user;
    }
}
