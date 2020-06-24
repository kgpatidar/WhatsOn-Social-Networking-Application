package com.example.whatson;

public class Chat {

    private String messagekey;
    private String message;
    private String senderId;
    private String recieverId;
    private String timeSend;

    public Chat() { }

    public Chat(String messagekey, String message, String senderId, String recieverId, String timeSend) {
        this.messagekey = messagekey;
        this.message = message;
        this.senderId = senderId;
        this.recieverId = recieverId;
        this.timeSend = timeSend;
    }

    public String getMessagekey() {
        return messagekey;
    }

    public void setMessagekey(String messagekey) {
        this.messagekey = messagekey;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRecieverId() {
        return recieverId;
    }

    public void setRecieverId(String recieverId) {
        this.recieverId = recieverId;
    }

    public String getTimeSend() {
        return timeSend;
    }

    public void setTimeSend(String timeSend) {
        this.timeSend = timeSend;
    }
}
