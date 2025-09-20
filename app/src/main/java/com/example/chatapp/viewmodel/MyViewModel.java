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

import java.util.List;

public class MyViewModel extends AndroidViewModel {

    Repository repository;

    public MyViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository();
    }

    //Auth
    public void signUpAnonymousUser(){
        Context c = this.getApplication();
        repository.firebaseAnonymousAuth(c);
    }

    public String getCurrentUserId(){
        return repository.getCurrentUserId();
    }

    public void signOut(){
        repository.signOut();
    }

    //Getting Chat Groups
    public MutableLiveData<List<ChatGroups>> getGroupsList(){
        return  repository.getChatGroupMutableLiveData();
    }

    //Creating New Groups
    public void createNewGroup(String groupName){
        repository.createNewChatGroup(groupName);
    }

    //Message
    public MutableLiveData<List<ChatMessage>> getMessageMutableLiveData(String groupName){
        return repository.getChatMessageMutableLiveData(groupName);
    }

    //Sending Messages
    public void sendMessage(String messsageText , String chatGroup){
        repository.sendMessage(messsageText,chatGroup);
    }


    }
