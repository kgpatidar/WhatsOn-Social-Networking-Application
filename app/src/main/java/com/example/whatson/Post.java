package com.example.whatson;

public class Post {

    private String postUniqueId;
    private String postCaption;
    private String postImgUrl;
    private String postDate;
    private String postLike;

    public Post(String postUniqueId, String postCaption, String postImgUrl, String postDate, String postLike) {
        this.postUniqueId = postUniqueId;
        this.postCaption = postCaption;
        this.postImgUrl = postImgUrl;
        this.postDate = postDate;
        this.postLike = postLike;
    }

    public Post() {}

    public String getPostUniqueId() {
        return postUniqueId;
    }

    public void setPostUniqueId(String postUniqueId) {
        this.postUniqueId = postUniqueId;
    }

    public String getPostCaption() {
        return postCaption;
    }

    public void setPostCaption(String postCaption) {
        this.postCaption = postCaption;
    }

    public String getPostImgUrl() {
        return postImgUrl;
    }

    public void setPostImgUrl(String postImgUrl) {
        this.postImgUrl = postImgUrl;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getPostLike() {
        return postLike;
    }

    public void setPostLike(String postLike) {
        this.postLike = postLike;
    }
}
