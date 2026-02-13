package com.example.chatapp.views;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.adapters.UserAdapter;
import com.example.chatapp.model.User;
import com.example.chatapp.viewmodel.MyViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AddMembersActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private EditText searchBar;
    private RecyclerView usersRecyclerView;
    private FloatingActionButton addMembersFab;

    private UserAdapter userAdapter;
    private MyViewModel myViewModel;

    private String groupId;
    private String groupName;
    private ArrayList<String> existingMemberIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_members);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get group details from intent
        groupId = getIntent().getStringExtra("GROUP_ID");
        groupName = getIntent().getStringExtra("GROUP_NAME");
        existingMemberIds = getIntent().getStringArrayListExtra("EXISTING_MEMBER_IDS");

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        searchBar = findViewById(R.id.searchBar);
        usersRecyclerView = findViewById(R.id.usersRecyclerView);
        addMembersFab = findViewById(R.id.addMembersFab);

        // Setup toolbar
        toolbar.setTitle("Add Members to " + groupName);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Setup RecyclerView
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize ViewModel
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);

        // Load all users
        loadUsers();

        // Setup FAB click listener
        addMembersFab.setOnClickListener(v -> addSelectedMembers());

        // Setup search functionality using TextWatcher
        searchBar.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (userAdapter != null) {
                    userAdapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
            }
        });
    }

    private void loadUsers() {
        myViewModel.getAllUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                if (users != null) {
                    // Filter out existing members
                    List<User> availableUsers = new ArrayList<>();
                    for (User user : users) {
                        if (!existingMemberIds.contains(user.getUserId())) {
                            availableUsers.add(user);
                        }
                    }

                    // Setup adapter
                    userAdapter = new UserAdapter(availableUsers);
                    usersRecyclerView.setAdapter(userAdapter);
                }
            }
        });
    }

    private void addSelectedMembers() {
        List<String> selectedUserIds = userAdapter.getSelectedUserIds();

        if (selectedUserIds.isEmpty()) {
            Toast.makeText(this, "Please select at least one member", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add members to group
        myViewModel.addMembersToGroup(groupId, selectedUserIds, new MyViewModel.OnGroupUpdateListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(AddMembersActivity.this,
                        selectedUserIds.size() + " member(s) added successfully", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AddMembersActivity.this,
                        "Failed to add members: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
