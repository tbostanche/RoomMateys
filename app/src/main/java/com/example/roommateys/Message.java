package com.example.roommateys;

public class Message {
    private String displayName;
    private String messageText;
    private String uid;
    public Message() {

    }
    public Message(String displayName,String messageText,String uid) {
        this.displayName = displayName;
        this.messageText = messageText;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
}
