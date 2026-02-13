package com.example.chatapp.views;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.adapters.MemberAdapter;
import com.example.chatapp.model.ChatGroups;
import com.example.chatapp.model.User;
import com.example.chatapp.viewmodel.MyViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class GroupMembersActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView membersRecyclerView;
    private MemberAdapter memberAdapter;
    private MyViewModel myViewModel;

    private String groupId;
    private String groupName;
    private String creatorId;
    private List<String> memberIds;
    private List<User> membersList;
    private boolean isCreator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_group_members);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get group details from intent
        groupId = getIntent().getStringExtra("GROUP_ID");
        groupName = getIntent().getStringExtra("GROUP_NAME");
        creatorId = getIntent().getStringExtra("CREATOR_ID");
        memberIds = getIntent().getStringArrayListExtra("MEMBER_IDS");

        // Check if current user is the creator
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        isCreator = currentUserId.equals(creatorId);

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        membersRecyclerView = findViewById(R.id.membersRecyclerView);

        // Setup toolbar
        toolbar.setTitle(groupName + " - Members");
        toolbar.setNavigationOnClickListener(v -> finish());

        // Setup RecyclerView
        membersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        membersList = new ArrayList<>();

        // Initialize ViewModel
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);

        // Load members
        loadMembers();
    }

    private void loadMembers() {
        if (memberIds == null || memberIds.isEmpty()) {
            Toast.makeText(this, "No members found", Toast.LENGTH_SHORT).show();
            return;
        }

        membersList.clear();

        // Fetch each member's details
        for (String memberId : memberIds) {
            myViewModel.getUserProfile(memberId, new com.example.chatapp.Repository.Repository.UserProfileCallback() {
                @Override
                public void onUserProfileLoaded(User user) {
                    if (user != null) {
                        membersList.add(user);

                        // Update adapter when all members are loaded
                        if (membersList.size() == memberIds.size()) {
                            setupAdapter();
                        }
                    }
                }
            });
        }
    }

    private void setupAdapter() {
        memberAdapter = new MemberAdapter(membersList, isCreator, creatorId,
                new MemberAdapter.OnMemberActionListener() {
                    @Override
                    public void onRemoveMember(User user) {
                        showRemoveMemberDialog(user);
                    }
                });
        membersRecyclerView.setAdapter(memberAdapter);
    }

    private void showRemoveMemberDialog(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Remove Member")
                .setMessage("Are you sure you want to remove " + user.getUsername() + " from this group?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    removeMember(user);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void removeMember(User user) {
        myViewModel.removeMemberFromGroup(groupId, user.getUserId(), new MyViewModel.OnGroupUpdateListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(GroupMembersActivity.this,
                        user.getUsername() + " removed from group", Toast.LENGTH_SHORT).show();

                // Remove from local list and update adapter
                memberIds.remove(user.getUserId());
                membersList.remove(user);
                memberAdapter.notifyDataSetChanged();

                // TODO: Send notification to removed member
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(GroupMembersActivity.this,
                        "Failed to remove member: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
