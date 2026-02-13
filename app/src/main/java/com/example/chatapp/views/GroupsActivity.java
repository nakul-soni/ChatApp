package com.example.chatapp.views;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.adapters.GroupAdapter;
import com.example.chatapp.R;
import com.example.chatapp.databinding.ActivityGroupsBinding;
import com.example.chatapp.model.ChatGroups;
import com.example.chatapp.viewmodel.MyViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class GroupsActivity extends AppCompatActivity {

    private ArrayList<ChatGroups> chatGroupsArrayList;
    private RecyclerView recyclerView;
    private GroupAdapter groupAdapter;
    private ActivityGroupsBinding activityGroupsBinding;
    private MyViewModel myViewModel;

    // Dialog
    private Dialog chatGroupDialog;

    private FloatingActionButton Fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_groups);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        activityGroupsBinding = DataBindingUtil.setContentView(this, R.layout.activity_groups);

        // Defining the ViewModel
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);

        // RecyclerView with DataBinding
        recyclerView = activityGroupsBinding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup an Observer to listen to the changes in the Livedata Object
        myViewModel.getGroupsList().observe(this, new Observer<List<ChatGroups>>() {
            @Override
            public void onChanged(List<ChatGroups> chatGroups) {
                // The updated data is recieved as 'chatGroups' parameter in onChanged()

                chatGroupsArrayList = new ArrayList<>();
                chatGroupsArrayList.addAll(chatGroups);

                groupAdapter = new GroupAdapter(chatGroupsArrayList);

                recyclerView.setAdapter(groupAdapter);
                groupAdapter.notifyDataSetChanged();
            }
        });

        activityGroupsBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        // Navigation drawer (hamburger menu)
        activityGroupsBinding.hamburger.setOnClickListener(v -> {
            if (activityGroupsBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                activityGroupsBinding.drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                activityGroupsBinding.drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        activityGroupsBinding.navView
                .setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.nav_logout) {
                            Toast.makeText(GroupsActivity.this, "Logging Out...", Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(GroupsActivity.this, LoginActivity.class));
                            finish();
                        }
                        return true;
                    }
                });

        // Load current user profile and display in drawer header
        myViewModel.getCurrentUserProfile().observe(this, new Observer<com.example.chatapp.model.User>() {
            @Override
            public void onChanged(com.example.chatapp.model.User user) {
                if (user != null) {
                    // Get the header view and bind user data
                    View headerView = activityGroupsBinding.navView.getHeaderView(0);
                    if (headerView != null) {
                        com.example.chatapp.databinding.DrawerHeaderBinding headerBinding = com.example.chatapp.databinding.DrawerHeaderBinding
                                .bind(headerView);
                        headerBinding.setUser(user);
                    }
                }
            }
        });

    }

    public void showDialog() {
        chatGroupDialog = new Dialog(this);
        chatGroupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_layout,
                        null);
        chatGroupDialog.setContentView(view);
        chatGroupDialog.show();

        Button submit = view.findViewById(R.id.submit_btn);
        EditText edt = view.findViewById(R.id.chat_group_edt);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = edt.getText().toString().trim();

                if (groupName.isEmpty()) {
                    Toast.makeText(GroupsActivity.this, "Please enter a group name", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Navigate to SelectMembersActivity
                Intent intent = new Intent(GroupsActivity.this, SelectMembersActivity.class);
                intent.putExtra("GROUP_NAME", groupName);
                startActivity(intent);
                chatGroupDialog.dismiss();
            }
        });
    }

}