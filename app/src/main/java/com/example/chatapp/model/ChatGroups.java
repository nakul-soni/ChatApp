package com.example.chatapp.model;

public class ChatGroups {

    String groupName;

    //Constructor
    public ChatGroups(String groupName) {
        this.groupName = groupName;
    }

    //Getters and Setters
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
