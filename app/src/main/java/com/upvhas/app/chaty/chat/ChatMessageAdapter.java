package com.upvhas.app.chaty.chat;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.upvhas.app.chaty.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 9/12/2016.
 */

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageViewHolder> {

    private Activity mActivity;

    List<Message> mMessages = new ArrayList<>();
    public ChatMessageAdapter(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void addMessage(Message message){
        mMessages.add(message);
        // Notify to all observers
        notifyItemInserted(mMessages.size());
    }

    @Override
    public ChatMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatMessageViewHolder(mActivity.getLayoutInflater().inflate(R.layout.item_chat,parent,false));
    }

    @Override
    public void onBindViewHolder(ChatMessageViewHolder holder, int position) {
        holder.bind(mMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }
}
