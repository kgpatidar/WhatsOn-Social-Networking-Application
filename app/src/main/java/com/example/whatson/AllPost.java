package com.example.whatson;

public class AllPost {

    private  String idOfUser;
    private String idOfPost;

    public AllPost(String idOfUser, String idOfPost) {
        this.idOfPost = idOfPost;
        this.idOfUser = idOfUser;
    }

    public AllPost() {    }

    public String getIdOfUser() {
        return idOfUser;
    }

    public void setIdOfUser(String idOfUser) {
        this.idOfUser = idOfUser;
    }

    public String getIdOfPost() {
        return idOfPost;
    }

    public void setIdOfPost(String idOfPost) {
        this.idOfPost = idOfPost;
    }
}
