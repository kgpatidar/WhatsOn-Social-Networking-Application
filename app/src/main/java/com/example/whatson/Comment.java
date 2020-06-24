package com.example.whatson;

public class Comment {

    private String commentUniqueID;
    private String commentText;
    private String commentDate;
    private String commentUser;

    public Comment(String commentUniqueID, String commentUser,String commentText, String commentDate) {
        this.commentUniqueID = commentUniqueID;
        this.commentUser = commentUser;
        this.commentText = commentText;
        this.commentDate = commentDate;
    }

    public Comment() { }

    public String getCommentUser() {
        return commentUser;
    }

    public void setCommentUser(String commentUser) {
        this.commentUser = commentUser;
    }

    public String getCommentUniqueID() {
        return commentUniqueID;
    }

    public void setCommentUniqueID(String commentUniqueID) {
        this.commentUniqueID = commentUniqueID;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }
}
