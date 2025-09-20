package com.example.chatapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.BR;
import com.example.chatapp.R;
import com.example.chatapp.databinding.RowChatBinding;
import com.example.chatapp.model.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private List<ChatMessage> chatMessageList;
    private Context context;

    //Constructor
    public ChatAdapter(List<ChatMessage> chatMessageList, Context context) {
        this.chatMessageList = chatMessageList;
        this.context = context;
    }

    //Implemented Methods
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.row_chat,parent,false);
        RowChatBinding rowChatBinding = DataBindingUtil.bind(view);

        return new MyViewHolder(rowChatBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.getRowChatBinding().setVariable(BR.chatMessage,chatMessageList.get(position));
        holder.getRowChatBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }


    //ViewHolder
    public class MyViewHolder extends RecyclerView.ViewHolder{

        private RowChatBinding rowChatBinding;

        //Constructor

        public MyViewHolder(RowChatBinding rowChatBinding) {
            super(rowChatBinding.getRoot());
            setRowChatBinding(rowChatBinding);
        }

        //Getters and Setters
        public RowChatBinding getRowChatBinding() {
            return rowChatBinding;
        }

        public void setRowChatBinding(RowChatBinding rowChatBinding) {
            this.rowChatBinding = rowChatBinding;
        }
    }
}
