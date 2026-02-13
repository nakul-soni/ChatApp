package com.example.chatapp.viewmodel;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.chatapp.Repository.Repository;
import com.example.chatapp.model.ChatGroups;
import com.example.chatapp.model.ChatMessage;
import com.example.chatapp.model.User;

import java.util.List;

public class MyViewModel extends AndroidViewModel {

    Repository repository;

    public MyViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository();
    }

    // Auth
    public void signUpAnonymousUser() {
        Context c = this.getApplication();
        repository.firebaseAnonymousAuth(c);
    }

    public String getCurrentUserId() {
        return repository.getCurrentUserId();
    }

    public void signOut() {
        repository.signOut();
    }

    // Getting Chat Groups
    public MutableLiveData<List<ChatGroups>> getGroupsList() {
        return repository.getChatGroupMutableLiveData();
    }

    // Creating New Groups
    public void createNewGroup(String groupName, List<String> memberIds, String creatorId, String creatorUsername) {
        repository.createNewChatGroup(groupName, memberIds, creatorId, creatorUsername);
    }

    // Message
    public MutableLiveData<List<ChatMessage>> getMessageMutableLiveData(String groupName) {
        return repository.getChatMessageMutableLiveData(groupName);
    }

    // Sending Messages
    public void sendMessage(String messsageText, String chatGroup) {
        repository.sendMessage(messsageText, chatGroup);
    }

    // ========== USER PROFILE METHODS ==========

    // Get current user profile
    public MutableLiveData<User> getCurrentUserProfile() {
        return repository.getCurrentUserProfile();
    }

    // Save user profile
    public void saveUserProfile(User user) {
        repository.saveUserProfile(user);
    }

    // Get all users
    public MutableLiveData<List<User>> getAllUsers() {
        return repository.getAllUsers();
    }

    // Get user profile by ID
    public void getUserProfile(String userId, Repository.UserProfileCallback callback) {
        repository.getUserProfile(userId, callback);
    }

    // ========== MEMBER MANAGEMENT METHODS ==========

    // Remove member from group
    public void removeMemberFromGroup(String groupId, String memberId, OnGroupUpdateListener listener) {
        repository.removeMemberFromGroup(groupId, memberId, new Repository.OnGroupUpdateCallback() {
            @Override
            public void onSuccess() {
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(String error) {
                if (listener != null) {
                    listener.onFailure(error);
                }
            }
        });
    }

    // Callback interface for UI
    public interface OnGroupUpdateListener {
        void onSuccess();

        void onFailure(String error);
    }

    // Add members to existing group
    public void addMembersToGroup(String groupId, List<String> newMemberIds, OnGroupUpdateListener listener) {
        repository.addMembersToGroup(groupId, newMemberIds, new Repository.OnGroupUpdateCallback() {
            @Override
            public void onSuccess() {
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(String error) {
                if (listener != null) {
                    listener.onFailure(error);
                }
            }
        });
    }

    // Delete group
    public void deleteGroup(String groupId, OnGroupUpdateListener listener) {
        repository.deleteGroup(groupId, new Repository.OnGroupUpdateCallback() {
            @Override
            public void onSuccess() {
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(String error) {
                if (listener != null) {
                    listener.onFailure(error);
                }
            }
        });
    }

}
