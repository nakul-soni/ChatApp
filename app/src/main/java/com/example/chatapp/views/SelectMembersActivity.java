package com.example.chatapp.views;

import android.content.Intent;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class SelectMembersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private MyViewModel myViewModel;
    private FloatingActionButton fabCreateGroup;
    private EditText searchEditText;
    private String groupName;
    private List<User> allUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_members);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get group name from intent
        groupName = getIntent().getStringExtra("GROUP_NAME");
        if (groupName == null || groupName.isEmpty()) {
            Toast.makeText(this, "Error: No group name provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        recyclerView = findViewById(R.id.members_recycler_view);
        fabCreateGroup = findViewById(R.id.fab_create_group);
        searchEditText = findViewById(R.id.search_members);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        allUsers = new ArrayList<>();

        // Initialize ViewModel
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);

        // Load all users
        myViewModel.getAllUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                allUsers = users;
                userAdapter = new UserAdapter(users);
                recyclerView.setAdapter(userAdapter);
            }
        });

        // Search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
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
            public void afterTextChanged(Editable s) {
            }
        });

        // Create group button
        fabCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userAdapter != null) {
                    List<String> selectedMemberIds = userAdapter.getSelectedUserIds();

                    if (selectedMemberIds.isEmpty()) {
                        Toast.makeText(SelectMembersActivity.this,
                                "Please select at least one member", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Get current user info
                    myViewModel.getCurrentUserProfile().observe(SelectMembersActivity.this, new Observer<User>() {
                        @Override
                        public void onChanged(User currentUser) {
                            if (currentUser != null) {
                                // Create the group
                                myViewModel.createNewGroup(
                                        groupName,
                                        selectedMemberIds,
                                        currentUser.getUserId(),
                                        currentUser.getUsername());

                                Toast.makeText(SelectMembersActivity.this,
                                        "Group created successfully!", Toast.LENGTH_SHORT).show();

                                // Go back to Groups screen
                                Intent intent = new Intent(SelectMembersActivity.this, GroupsActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }
        });

        // Back button
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
    }
}
