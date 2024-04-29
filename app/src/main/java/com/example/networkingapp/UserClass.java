package com.example.networkingapp;

public class UserClass {
    private String userName;
    private String userDesc;
    private String userImage;
    private String userEmail;
    private String userID;
    private Boolean userIsStartup;

    public UserClass(String userName, String userDesc, String userImage,
                     String userEmail, String userID, Boolean userIsStartup){
        this.userName = userName;
        this.userDesc = userDesc;
        this.userImage = userImage;
        this.userEmail = userEmail;
        this.userID = userID;
        this.userIsStartup = userIsStartup;
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

    public UserClass(){}
}
