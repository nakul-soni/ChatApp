package com.example.chatapp.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.databinding.ItemCardBinding;
import com.example.chatapp.model.ChatGroups;
import com.example.chatapp.views.ChatsActivity;

import java.util.ArrayList;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private ArrayList<ChatGroups> groupsArrayList;

    //Constructor
    public GroupAdapter(ArrayList<ChatGroups> groupsArrayList) {
        this.groupsArrayList = groupsArrayList;
    }

    //Implemented Methods
    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCardBinding itemCardBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_card,
                parent,
                false
        );
        return new GroupViewHolder(itemCardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        //Binds data to an existing viewholder
        //Populates the views in the view holder with the data in the data set
        ChatGroups currentUser = groupsArrayList.get(position);
        holder.itemCardBinding.setChatGroup(currentUser);
    }

    @Override
    public int getItemCount() {
        return groupsArrayList.size();
    }


    //ViewHolder
    public class GroupViewHolder extends RecyclerView.ViewHolder{
        //This Viewholder cache the references to the individual views within an item layout of the
        //Recycler view List

        ItemCardBinding itemCardBinding;

        //Constructor
        public GroupViewHolder(ItemCardBinding itemCardBinding) {
            super(itemCardBinding.getRoot());
            this.itemCardBinding = itemCardBinding;

            itemCardBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    ChatGroups clickedChatGroup = groupsArrayList.get(position);
                    Intent i = new Intent(v.getContext(), ChatsActivity.class);
                    i.putExtra("GROUP_NAME",clickedChatGroup.getGroupName());
                    v.getContext().startActivity(i);
                }

            });
        }
    }

}
