package com.upvhas.app.chaty.chat;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.upvhas.app.chaty.R;

/**
 * Created by user on 9/12/2016.
 */

public class ChatMessageViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "ChatMessageViewHolder";
    private final Activity mActivity;

    TextView authorTextview, textMessageTextView;
    ImageView imageMessageImageView;

    public ChatMessageViewHolder(Activity activity,View itemView) {
        super(itemView);
        mActivity = activity;
        authorTextview = (TextView) itemView.findViewById(R.id.message_author);
        textMessageTextView = (TextView) itemView.findViewById(R.id.message_TextView);
        imageMessageImageView = (ImageView) itemView.findViewById(R.id.messageImageView);
    }

    public void bind(GoatMessage message){
        boolean isPhoto = message.getPhotoUrl() != null;
        if(isPhoto){
            textMessageTextView.setVisibility(View.GONE);
            imageMessageImageView.setVisibility(View.VISIBLE);
            // GLIDE
        }else{
            imageMessageImageView.setVisibility(View.GONE);
            textMessageTextView.setVisibility(View.VISIBLE);
            textMessageTextView.setText(message.getTextMessage());
        }
        authorTextview.setText(message.getAutor());
    }

}
