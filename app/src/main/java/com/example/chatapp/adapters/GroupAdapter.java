package com.example.chatapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.databinding.ItemCardBinding;
import com.example.chatapp.model.ChatGroups;
import com.example.chatapp.views.ChatsActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private ArrayList<ChatGroups> groupsArrayList;

    // Constructor
    public GroupAdapter(ArrayList<ChatGroups> groupsArrayList) {
        this.groupsArrayList = groupsArrayList;
    }

    // Implemented Methods
    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCardBinding itemCardBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_card,
                parent,
                false);
        return new GroupViewHolder(itemCardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        // Binds data to an existing viewholder
        // Populates the views in the view holder with the data in the data set
        ChatGroups currentUser = groupsArrayList.get(position);
        holder.itemCardBinding.setChatGroup(currentUser);
    }

    @Override
    public int getItemCount() {
        return groupsArrayList.size();
    }

    // ViewHolder
    public class GroupViewHolder extends RecyclerView.ViewHolder {
        // This Viewholder cache the references to the individual views within an item
        // layout of the
        // Recycler view List

        ItemCardBinding itemCardBinding;

        // Constructor
        public GroupViewHolder(ItemCardBinding itemCardBinding) {
            super(itemCardBinding.getRoot());
            this.itemCardBinding = itemCardBinding;

            // Click listener - Open chat
            itemCardBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    ChatGroups clickedChatGroup = groupsArrayList.get(position);
                    Intent i = new Intent(v.getContext(), ChatsActivity.class);
                    i.putExtra("GROUP_ID", clickedChatGroup.getGroupId());
                    i.putExtra("GROUP_NAME", clickedChatGroup.getGroupName());
                    v.getContext().startActivity(i);
                }

            });

            // Long-press listener - View members and more options
            itemCardBinding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    ChatGroups clickedChatGroup = groupsArrayList.get(position);

                    // Check if current user is creator
                    String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    boolean isCreator = currentUserId.equals(clickedChatGroup.getCreatorId());

                    // Create menu dialog
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(v.getContext());
                    builder.setTitle(clickedChatGroup.getGroupName());

                    // Different options for creator vs member
                    String[] options;
                    if (isCreator) {
                        options = new String[] { "View Members", "Add Members", "Delete Group", "Cancel" };
                    } else {
                        options = new String[] { "View Members", "Leave Group", "Cancel" };
                    }

                    builder.setItems(options, (dialog, which) -> {
                        if (isCreator) {
                            handleCreatorAction(v.getContext(), which, clickedChatGroup, position);
                        } else {
                            handleMemberAction(v.getContext(), which, clickedChatGroup, position);
                        }
                    });
                    builder.show();
                    return true;
                }
            });
        }

        private void handleCreatorAction(Context context, int which, ChatGroups group, int position) {
            switch (which) {
                case 0: // View Members
                    Intent viewIntent = new Intent(context, com.example.chatapp.views.GroupMembersActivity.class);
                    viewIntent.putExtra("GROUP_ID", group.getGroupId());
                    viewIntent.putExtra("GROUP_NAME", group.getGroupName());
                    viewIntent.putExtra("CREATOR_ID", group.getCreatorId());
                    viewIntent.putStringArrayListExtra("MEMBER_IDS", new ArrayList<>(group.getMemberIds()));
                    context.startActivity(viewIntent);
                    break;

                case 1: // Add Members
                    Intent addIntent = new Intent(context, com.example.chatapp.views.AddMembersActivity.class);
                    addIntent.putExtra("GROUP_ID", group.getGroupId());
                    addIntent.putExtra("GROUP_NAME", group.getGroupName());
                    addIntent.putStringArrayListExtra("EXISTING_MEMBER_IDS", new ArrayList<>(group.getMemberIds()));
                    context.startActivity(addIntent);
                    break;

                case 2: // Delete Group
                    showDeleteConfirmation(context, group, position);
                    break;
            }
        }

        private void handleMemberAction(Context context, int which, ChatGroups group, int position) {
            switch (which) {
                case 0: // View Members
                    Intent viewIntent = new Intent(context, com.example.chatapp.views.GroupMembersActivity.class);
                    viewIntent.putExtra("GROUP_ID", group.getGroupId());
                    viewIntent.putExtra("GROUP_NAME", group.getGroupName());
                    viewIntent.putExtra("CREATOR_ID", group.getCreatorId());
                    viewIntent.putStringArrayListExtra("MEMBER_IDS", new ArrayList<>(group.getMemberIds()));
                    context.startActivity(viewIntent);
                    break;

                case 1: // Leave Group
                    showLeaveConfirmation(context, group, position);
                    break;
            }
        }

        private void showDeleteConfirmation(Context context, ChatGroups group, int position) {
            new android.app.AlertDialog.Builder(context)
                    .setTitle("Delete Group")
                    .setMessage("Are you sure you want to delete \"" + group.getGroupName()
                            + "\"? This will remove the group for all members.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        Toast.makeText(context, "Delete group functionality coming soon", Toast.LENGTH_SHORT).show();
                        // TODO: Implement in Repository
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }

        private void showLeaveConfirmation(Context context, ChatGroups group, int position) {
            new android.app.AlertDialog.Builder(context)
                    .setTitle("Leave Group")
                    .setMessage("Are you sure you want to leave \"" + group.getGroupName() + "\"?")
                    .setPositiveButton("Leave", (dialog, which) -> {
                        Toast.makeText(context, "Leave group functionality coming soon", Toast.LENGTH_SHORT).show();
                        // TODO: Implement using removeMemberFromGroup
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

}
