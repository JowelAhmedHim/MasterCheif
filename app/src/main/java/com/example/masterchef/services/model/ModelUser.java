package com.example.masterchef.services.model;

public class ModelUser {

    private String email,name,online,profileImage,uid,timestamp,accountType;

    public ModelUser() {
    }

    public ModelUser(String email, String name, String online, String profileImage, String uid, String timestamp, String accountType) {
        this.email = email;
        this.name = name;
        this.online = online;
        this.profileImage = profileImage;
        this.uid = uid;
        this.timestamp = timestamp;
        this.accountType = accountType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}
