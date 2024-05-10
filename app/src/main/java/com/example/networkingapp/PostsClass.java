package com.example.networkingapp;

public class PostsClass {
    private String postText;
    private String postImage;
    private String authorID;
    private String authorName;
    private Object timestamp;

    public PostsClass(String postText, String postImage, String authorID, String authorName, Object timestamp) {
        this.postText = postText;
        this.postImage = postImage;
        this.authorID = authorID;
        this.authorName = authorName;
        this.timestamp = timestamp;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public String getPostText() {
        return postText;
    }

    public String getPostImage() {
        return postImage;
    }

    public String getAuthorID() {
        return authorID;
    }
    public String getAuthorName() {
        return authorName;
    }
    public Object getTimestamp() {
        return timestamp;
    }

    public PostsClass(){

    }


}
