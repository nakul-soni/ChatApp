package com.example.chatapp.views;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.adapters.ChatAdapter;
import com.example.chatapp.databinding.ActivityChatsBinding;
import com.example.chatapp.model.ChatMessage;
import com.example.chatapp.viewmodel.MyViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChatsActivity extends AppCompatActivity {
    private ActivityChatsBinding activityChatsBinding;
    private MyViewModel myViewModel;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;

    private List<ChatMessage> messageList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chats);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        activityChatsBinding = DataBindingUtil.setContentView(this,R.layout.activity_chats);
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);

        //RecyclerView With DataBinding
        recyclerView = activityChatsBinding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //Getting groups name from the the clicked item in the GroupActivity
        String groupName = getIntent().getStringExtra("GROUP_NAME");

        myViewModel.getMessageMutableLiveData(groupName).observe(this, new Observer<List<ChatMessage>>() {
            @Override
            public void onChanged(List<ChatMessage> chatMessages) {
                messageList = new ArrayList<>();
                messageList.addAll(chatMessages);

                chatAdapter = new ChatAdapter(messageList,getApplicationContext());
                recyclerView.setAdapter(chatAdapter);
                chatAdapter.notifyDataSetChanged();

                //Scroll to the latest message added
                int latestPosition = chatAdapter.getItemCount()-1;

                if (latestPosition>0) {
                    recyclerView.smoothScrollToPosition(latestPosition);
            }
            }
        });

        activityChatsBinding.setVModel(myViewModel);

        activityChatsBinding.sendBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = activityChatsBinding.edittextChatMessage.getText().toString();
                myViewModel.sendMessage(msg,groupName);
                activityChatsBinding.edittextChatMessage.getText().clear();
            }
        });
    }
}