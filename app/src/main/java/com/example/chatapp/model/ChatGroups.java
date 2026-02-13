package com.example.chatapp.model;

import java.util.ArrayList;
import java.util.List;

public class ChatGroups {

    String groupId;
    String groupName;
    String creatorId;
    String creatorUsername;
    List<String> memberIds;
    long createdAt;

    // Empty Constructor (required for Firebase)
    public ChatGroups() {
        this.memberIds = new ArrayList<>();
    }

    // Constructor with group name only (for backward compatibility)
    public ChatGroups(String groupName) {
        this.groupName = groupName;
        this.memberIds = new ArrayList<>();
    }

    // Full Constructor
    public ChatGroups(String groupId, String groupName, String creatorId, String creatorUsername,
            List<String> memberIds, long createdAt) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.creatorId = creatorId;
        this.creatorUsername = creatorUsername;
        this.memberIds = memberIds != null ? memberIds : new ArrayList<>();
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }

    public List<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    // Helper method to add a member
    public void addMember(String userId) {
        if (!memberIds.contains(userId)) {
            memberIds.add(userId);
        }
    }

    // Helper method to remove a member
    public void removeMember(String userId) {
        memberIds.remove(userId);
    }

    // Check if user is a member
    public boolean isMember(String userId) {
        return memberIds.contains(userId);
    }

    // Check if user is the creator
    public boolean isCreator(String userId) {
        return creatorId != null && creatorId.equals(userId);
    }
}
