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

    private static final String TAG = "ChatMessageAdapter";
    private Activity mActivity;
    List<GoatMessage> mGoatMessages = new ArrayList<>();

    public ChatMessageAdapter(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void addMessage(GoatMessage message){
        mGoatMessages.add(message);
        notifyItemInserted(mGoatMessages.size());
    }

    @Override
    public ChatMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatMessageViewHolder(mActivity,mActivity.getLayoutInflater().inflate(R.layout.item_chat,parent,false));
    }

    @Override
    public void onBindViewHolder(ChatMessageViewHolder holder, int position) {
        holder.bind(mGoatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return mGoatMessages.size();
    }
}
