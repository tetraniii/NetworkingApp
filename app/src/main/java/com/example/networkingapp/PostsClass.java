package com.example.networkingapp;

public class PostsClass {
    private String postText;
    private String postImage;
    private String authorID;
    private String postDate;

    public PostsClass(String postText, String postImage, String authorID, String postDate) {
        this.postText = postText;
        this.postImage = postImage;
        this.authorID = authorID;
        this.postDate = postDate;
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

    public String getPostDate() {
        return postDate;
    }

    public PostsClass(){

    }
}
