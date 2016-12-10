package com.upvhas.app.chaty.chat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.upvhas.app.chaty.R;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_chat_container,new ChatFragment())
                    .commit();
        }
    }
}
