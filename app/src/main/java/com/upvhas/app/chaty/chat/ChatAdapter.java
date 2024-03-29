package com.upvhas.app.chaty.chat;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.upvhas.app.chaty.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 27/12/2016.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatMessageViewHolder> {

    private Activity mActivity;
    private List<Message> listMessages = new ArrayList<>();

    public ChatAdapter(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void addMessage(Message message){
        listMessages.add(message);
        notifyItemInserted(listMessages.size());
    }

    @Override
    public ChatMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatMessageViewHolder(mActivity.getLayoutInflater().inflate(R.layout.item_chat,parent,false));
    }

    @Override
    public void onBindViewHolder(ChatMessageViewHolder holder, int position) {
        holder.setImageMessageImageView(listMessages.get(position).getPhotoUrl());
        holder.setTextMessageTextView(listMessages.get(position).getTextMessage());
        holder.setAuthorTextview(listMessages.get(position).getAutor());
    }

    @Override
    public int getItemCount() {
        return listMessages.size();
    }
}
