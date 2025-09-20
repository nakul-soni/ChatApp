package com.example.chatapp.Repository;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.chatapp.databinding.ActivityLoginBinding;
import com.example.chatapp.model.ChatMessage;
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

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference groupReference;
    MutableLiveData<List<ChatMessage>> chatMessageMutableLiveData;



    public Repository() {
        this.chatGroupMutableLiveData = new MutableLiveData<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        chatMessageMutableLiveData = new MutableLiveData<>();
    }



    // Anonymous Auth
    public void firebaseAnonymousAuth(Context context){
        FirebaseAuth.getInstance().signInAnonymously()
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            // Authentication is successful:
                            Intent i = new Intent(context, GroupsActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);

                        }

                    }
                });
    }

    //Getting the current userid
    public String getCurrentUserId() {
       return FirebaseAuth.getInstance().getUid();
    }

    //Signing out the User
    public void signOut(){
        FirebaseAuth.getInstance().signOut();
    }

    //Getting chat groups available from the Firebase Realtime database
    public MutableLiveData<List<ChatGroups>> getChatGroupMutableLiveData() {
        List<ChatGroups> groupsList = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupsList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()
                     ) {
                    ChatGroups groups = new ChatGroups(dataSnapshot.getKey());

                    groupsList.add(groups);
                }
                chatGroupMutableLiveData.postValue(groupsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return chatGroupMutableLiveData;
    }

    //Creating a New Group
    public void createNewChatGroup(String groupName){
        databaseReference.child(groupName).setValue(groupName);
    }

    //Getting Messages LiveData


    public MutableLiveData<List<ChatMessage>> getChatMessageMutableLiveData(String groupName) {
        groupReference = firebaseDatabase.getReference().child(groupName);
        List<ChatMessage> messageList = new ArrayList<>();

        groupReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();

                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                    messageList.add(message);
                }

                chatMessageMutableLiveData.postValue(messageList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return chatMessageMutableLiveData;
    }

    //Sending Chat Messages
    public void sendMessage(String messageText, String chatGroup){
        DatabaseReference ref = firebaseDatabase.getReference(chatGroup);

        if (!messageText.trim().equals("")){
            ChatMessage msg = new ChatMessage(
                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    messageText,
                    System.currentTimeMillis()
            );

            String randomKey = ref.push().getKey();
            ref.child(randomKey).setValue(msg);
        }


    }
}
