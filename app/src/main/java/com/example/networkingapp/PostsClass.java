package com.example.networkingapp;

public class PostsClass {
    //TODO: добавить дату создания поста
    private String postText;
    private String postImage;
    private String authorID;

    public PostsClass(String postText, String postImage, String authorID) {
        this.postText = postText;
        this.postImage = postImage;
        this.authorID = authorID;
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

    public PostsClass(){

    }
}
