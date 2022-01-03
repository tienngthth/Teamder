package com.example.teamder.model;

public final class UserProfile {
    private static UserProfile instance;
    private String name;
    private UserType type;
    private String uid;

    private UserProfile(User user) {
        this.name = user.getName();
        this.type = user.getType();
        this.uid = user.getUid();
    }

    public static UserProfile getInstance(User user) {
        if (instance == null && user != null) {
            instance = new UserProfile(user);
        }
        return instance;
    }

    public static void deleteInstance(){
        instance = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
