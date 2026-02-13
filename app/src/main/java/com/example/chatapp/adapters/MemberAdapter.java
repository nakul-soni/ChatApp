package com.example.chatapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.model.User;

import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private List<User> membersList;
    private boolean isCreator;
    private String creatorId;
    private OnMemberActionListener listener;

    public interface OnMemberActionListener {
        void onRemoveMember(User user);
    }

    public MemberAdapter(List<User> membersList, boolean isCreator, String creatorId, OnMemberActionListener listener) {
        this.membersList = membersList;
        this.isCreator = isCreator;
        this.creatorId = creatorId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.member_item, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        User user = membersList.get(position);

        holder.usernameTextView.setText(user.getUsername());
        holder.emailTextView.setText(user.getEmail());

        // Show "Creator" badge if this user is the creator
        if (user.getUserId().equals(creatorId)) {
            holder.roleTextView.setVisibility(View.VISIBLE);
            holder.roleTextView.setText("Creator");
            holder.removeButton.setVisibility(View.GONE);
        } else {
            holder.roleTextView.setVisibility(View.GONE);

            // Only show remove button if current user is creator
            if (isCreator) {
                holder.removeButton.setVisibility(View.VISIBLE);
                holder.removeButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onRemoveMember(user);
                    }
                });
            } else {
                holder.removeButton.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return membersList.size();
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView emailTextView;
        TextView roleTextView;
        ImageButton removeButton;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.memberUsername);
            emailTextView = itemView.findViewById(R.id.memberEmail);
            roleTextView = itemView.findViewById(R.id.memberRole);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }
}
