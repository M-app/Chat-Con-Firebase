package com.upvhas.app.chaty.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.upvhas.app.chaty.R;

/**
 * Created by user on 9/12/2016.
 */

public class ChatMessageViewHolder extends RecyclerView.ViewHolder {

    private TextView authorTextview, textMessageTextView;
    private ImageView imageMessageImageView;


    // inflate all views whitin the item_chat
    public ChatMessageViewHolder(View itemView) {
        super(itemView);
        authorTextview = (TextView) itemView.findViewById(R.id.message_author);
        textMessageTextView = (TextView) itemView.findViewById(R.id.message_TextView);
        imageMessageImageView = (ImageView) itemView.findViewById(R.id.messageImageView);
    }

    public void setAuthorTextview(String autor) {
        authorTextview.setText(autor);
    }

    public void setTextMessageTextView(String msg) {
        if(msg != null){
            textMessageTextView.setVisibility(View.VISIBLE);
            imageMessageImageView.setVisibility(View.GONE);
            textMessageTextView.setText(msg);
        }
    }

    public void setImageMessageImageView(String photoUrl) {
        if(photoUrl != null){
            textMessageTextView.setVisibility(View.GONE);
            imageMessageImageView.setVisibility(View.VISIBLE);
            StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(photoUrl);
            Glide.with(imageMessageImageView.getContext())
                    .using(new FirebaseImageLoader())
                    .load(imageRef)
                    .into(imageMessageImageView);
        }
    }
}
