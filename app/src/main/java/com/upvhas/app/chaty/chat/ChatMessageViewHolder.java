package com.upvhas.app.chaty.chat;

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

    private TextView authorTextview, textMessageTextView;
    private ImageView imageMessageImageView;

    // inflate all views whitin the item_chat
    public ChatMessageViewHolder(View itemView) {
        super(itemView);
        authorTextview = (TextView) itemView.findViewById(R.id.message_author);
        textMessageTextView = (TextView) itemView.findViewById(R.id.message_TextView);
        imageMessageImageView = (ImageView) itemView.findViewById(R.id.messageImageView);
    }

    // asigna a cada view lo que tiene que mostrar
    public void bind(Message message){
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
