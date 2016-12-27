package com.upvhas.app.chaty.salas;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.upvhas.app.chaty.R;

public class CreateChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat);
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_create_chat_container,new CreateChatFragment())
                    .commit();
        }
    }
}
