package com.example.whatson;

public class User {

    private String uniqueID;
    private String name;
    private String username;
    private String password;
    private String profileImageURL;
    private String dateofbirth;
    private String gender;
    private String numberOfPost;

    public User(String uniqueID, String name, String username, String password, String profileImageURL, String dateofbirth, String gender, String numberOfPost) {
        this.uniqueID = uniqueID;
        this.name = name;
        this.username = username;
        this.password = password;
        this.profileImageURL = profileImageURL;
        this.dateofbirth = dateofbirth;
        this.gender = gender;
        this.numberOfPost = numberOfPost;
    }

    public String getNumberOfPost() {
        return numberOfPost;
    }

    public void setNumberOfPost(String numberOfPost) {
        this.numberOfPost = numberOfPost;
    }

    public User() {  }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }

    public String getDateofbirth() {
        return dateofbirth;
    }

    public void setDateofbirth(String dateofbirth) {
        this.dateofbirth = dateofbirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
