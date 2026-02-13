package com.example.chatapp.Repository;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.chatapp.databinding.ActivityLoginBinding;
import com.example.chatapp.model.ChatMessage;
import com.example.chatapp.model.User;
import com.example.chatapp.views.GroupsActivity;
import com.example.chatapp.model.ChatGroups;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

//It Acts as a bridge between the viewmodel and the data sources
public class Repository {

    MutableLiveData<List<ChatGroups>> chatGroupMutableLiveData;
    MutableLiveData<User> currentUserMutableLiveData;
    MutableLiveData<List<User>> allUsersMutableLiveData;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference groupReference;
    DatabaseReference usersReference;
    MutableLiveData<List<ChatMessage>> chatMessageMutableLiveData;

    public Repository() {
        this.chatGroupMutableLiveData = new MutableLiveData<>();
        this.currentUserMutableLiveData = new MutableLiveData<>();
        this.allUsersMutableLiveData = new MutableLiveData<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        usersReference = firebaseDatabase.getReference("users");
        chatMessageMutableLiveData = new MutableLiveData<>();
    }

    // Anonymous Auth
    public void firebaseAnonymousAuth(Context context) {
        FirebaseAuth.getInstance().signInAnonymously()
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Authentication is successful:
                            Intent i = new Intent(context, GroupsActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);

                        }

                    }
                });
    }

    // Getting the current userid
    public String getCurrentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    // Signing out the User
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    // Getting chat groups available from the Firebase Realtime database
    // Now filters to show only groups where current user is a member
    public MutableLiveData<List<ChatGroups>> getChatGroupMutableLiveData() {
        List<ChatGroups> groupsList = new ArrayList<>();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to groups node
        DatabaseReference groupsRef = firebaseDatabase.getReference("groups");

        groupsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupsList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatGroups group = dataSnapshot.getValue(ChatGroups.class);

                    // Only add groups where current user is a member
                    if (group != null && group.getMemberIds() != null &&
                            group.getMemberIds().contains(currentUserId)) {
                        groupsList.add(group);
                    }
                }
                chatGroupMutableLiveData.postValue(groupsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
        return chatGroupMutableLiveData;
    }

    // Creating a New Group with members
    public void createNewChatGroup(String groupName, List<String> memberIds, String creatorId, String creatorUsername) {
        // Generate unique group ID
        DatabaseReference groupsRef = firebaseDatabase.getReference("groups");
        String groupId = groupsRef.push().getKey();

        // Ensure creator is in the member list
        if (!memberIds.contains(creatorId)) {
            memberIds.add(0, creatorId); // Add creator at the beginning
        }

        // Create ChatGroups object
        ChatGroups newGroup = new ChatGroups(
                groupId,
                groupName,
                creatorId,
                creatorUsername,
                memberIds,
                System.currentTimeMillis());

        // Save group to /groups/{groupId}
        groupsRef.child(groupId).setValue(newGroup);

        // Add group reference to each member's userGroups
        DatabaseReference userGroupsRef = firebaseDatabase.getReference("userGroups");
        for (String memberId : memberIds) {
            userGroupsRef.child(memberId).child(groupId).setValue(true);
        }
    }

    // Getting Messages LiveData
    // Updated to use groupId instead of groupName
    public MutableLiveData<List<ChatMessage>> getChatMessageMutableLiveData(String groupId) {
        groupReference = firebaseDatabase.getReference("groups").child(groupId).child("messages");
        List<ChatMessage> messageList = new ArrayList<>();

        groupReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                    if (message != null) {
                        messageList.add(message);
                    }
                }

                chatMessageMutableLiveData.postValue(messageList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
        return chatMessageMutableLiveData;
    }

    // Sending Chat Messages
    // Updated to use groupId and store messages under /groups/{groupId}/messages
    // Now includes sender username
    public void sendMessage(String messageText, String groupId) {
        DatabaseReference ref = firebaseDatabase.getReference("groups").child(groupId).child("messages");

        if (!messageText.trim().equals("")) {
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Get current user's username and send message
            usersReference.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String senderUsername = "Unknown";
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null && user.getUsername() != null) {
                            senderUsername = user.getUsername();
                        }
                    }

                    ChatMessage msg = new ChatMessage(
                            currentUserId,
                            senderUsername,
                            messageText,
                            System.currentTimeMillis());

                    String randomKey = ref.push().getKey();
                    ref.child(randomKey).setValue(msg);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Send message without username if fetch fails
                    ChatMessage msg = new ChatMessage(
                            currentUserId,
                            messageText,
                            System.currentTimeMillis());

                    String randomKey = ref.push().getKey();
                    ref.child(randomKey).setValue(msg);
                }
            });
        }

    }

    // ========== USER PROFILE METHODS ==========

    // Save user profile to database
    public void saveUserProfile(User user) {
        usersReference.child(user.getUserId()).setValue(user);
    }

    // Get current user profile
    public MutableLiveData<User> getCurrentUserProfile() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        usersReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    currentUserMutableLiveData.postValue(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        return currentUserMutableLiveData;
    }

    // Get user profile by ID
    public void getUserProfile(String userId, final UserProfileCallback callback) {
        usersReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    callback.onUserProfileLoaded(user);
                } else {
                    callback.onUserProfileLoaded(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onUserProfileLoaded(null);
            }
        });
    }

    // Get all users for member selection
    public MutableLiveData<List<User>> getAllUsers() {
        List<User> usersList = new ArrayList<>();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    // Don't include current user in the list
                    if (user != null && !user.getUserId().equals(currentUserId)) {
                        usersList.add(user);
                    }
                }
                allUsersMutableLiveData.postValue(usersList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        return allUsersMutableLiveData;
    }

    // Callback interface for getting user profile
    public interface UserProfileCallback {
        void onUserProfileLoaded(User user);
    }
    // ========== MEMBER MANAGEMENT METHODS ==========

    // Remove member from group
    public void removeMemberFromGroup(String groupId, String memberId, OnGroupUpdateCallback callback) {
        DatabaseReference groupRef = firebaseDatabase.getReference("groups").child(groupId);

        // Get current group data
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ChatGroups group = snapshot.getValue(ChatGroups.class);
                    if (group != null && group.getMemberIds() != null) {
                        // Remove member from memberIds list
                        group.getMemberIds().remove(memberId);

                        // Update group in database
                        groupRef.setValue(group)
                                .addOnSuccessListener(aVoid -> {
                                    // Remove group reference from user's userGroups
                                    DatabaseReference userGroupRef = firebaseDatabase.getReference("userGroups")
                                            .child(memberId).child(groupId);
                                    userGroupRef.removeValue()
                                            .addOnSuccessListener(aVoid1 -> {
                                                if (callback != null) {
                                                    callback.onSuccess();
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                if (callback != null) {
                                                    callback.onFailure(e.getMessage());
                                                }
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    if (callback != null) {
                                        callback.onFailure(e.getMessage());
                                    }
                                });
                    } else {
                        if (callback != null) {
                            callback.onFailure("Group not found or has no members");
                        }
                    }
                } else {
                    if (callback != null) {
                        callback.onFailure("Group not found");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (callback != null) {
                    callback.onFailure(error.getMessage());
                }
            }
        });
    }

    // Callback interface for group updates
    public interface OnGroupUpdateCallback {
        void onSuccess();

        void onFailure(String error);
    }

    // Add members to existing group
    public void addMembersToGroup(String groupId, List<String> newMemberIds, OnGroupUpdateCallback callback) {
        DatabaseReference groupRef = firebaseDatabase.getReference("groups").child(groupId);

        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ChatGroups group = snapshot.getValue(ChatGroups.class);
                    if (group != null && group.getMemberIds() != null) {
                        // Add new members to memberIds list
                        for (String memberId : newMemberIds) {
                            if (!group.getMemberIds().contains(memberId)) {
                                group.getMemberIds().add(memberId);
                            }
                        }

                        // Update group in database
                        groupRef.setValue(group)
                                .addOnSuccessListener(aVoid -> {
                                    // Add group reference to each new member's userGroups
                                    DatabaseReference userGroupsRef = firebaseDatabase.getReference("userGroups");
                                    for (String memberId : newMemberIds) {
                                        userGroupsRef.child(memberId).child(groupId).setValue(true);
                                    }
                                    if (callback != null) {
                                        callback.onSuccess();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    if (callback != null) {
                                        callback.onFailure(e.getMessage());
                                    }
                                });
                    } else {
                        if (callback != null) {
                            callback.onFailure("Group not found");
                        }
                    }
                } else {
                    if (callback != null) {
                        callback.onFailure("Group not found");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (callback != null) {
                    callback.onFailure(error.getMessage());
                }
            }
        });
    }

    // Delete group (creator only)
    public void deleteGroup(String groupId, OnGroupUpdateCallback callback) {
        DatabaseReference groupRef = firebaseDatabase.getReference("groups").child(groupId);

        // First get the group to find all members
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ChatGroups group = snapshot.getValue(ChatGroups.class);
                    if (group != null && group.getMemberIds() != null) {
                        // Remove group reference from all members' userGroups
                        DatabaseReference userGroupsRef = firebaseDatabase.getReference("userGroups");
                        for (String memberId : group.getMemberIds()) {
                            userGroupsRef.child(memberId).child(groupId).removeValue();
                        }

                        // Delete the group
                        groupRef.removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    if (callback != null) {
                                        callback.onSuccess();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    if (callback != null) {
                                        callback.onFailure(e.getMessage());
                                    }
                                });
                    } else {
                        if (callback != null) {
                            callback.onFailure("Group not found");
                        }
                    }
                } else {
                    if (callback != null) {
                        callback.onFailure("Group not found");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (callback != null) {
                    callback.onFailure(error.getMessage());
                }
            }
        });
    }

}
