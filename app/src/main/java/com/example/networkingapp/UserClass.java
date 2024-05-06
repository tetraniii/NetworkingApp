package com.example.networkingapp;

import java.util.ArrayList;
import java.util.List;

public class UserClass {
    private String userName;
    private String userDesc;
    private String userImage;
    private String userEmail;
    private String userID;
    private Boolean userIsStartup;
    private List<String> subscriptions;

    public UserClass(String userName, String userDesc, String userImage,
                     String userEmail, String userID, Boolean userIsStartup){
        this.userName = userName;
        this.userDesc = userDesc;
        this.userImage = userImage;
        this.userEmail = userEmail;
        this.userID = userID;
        this.userIsStartup = userIsStartup;
        this.subscriptions = new ArrayList<>();
    }

    public String getUserName() {
        return userName;
    }

    public String getUserDesc() {
        return userDesc;
    }

    public String getUserImage() {
        return userImage;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserID() {
        return userID;
    }

    public Boolean getUserIsStartup() {
        return userIsStartup;
    }
    public List<String> getSubscriptions(){
        return subscriptions;
    }
    public void addSubscription(String userID){
        subscriptions.add(userID);
    }
    public void removeSubscription(String userID){
        subscriptions.remove(userID);
    }
    public UserClass(){}
}
