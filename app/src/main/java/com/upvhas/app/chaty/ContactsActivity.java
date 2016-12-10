package com.upvhas.app.chaty;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.upvhas.app.chaty.chat.ChatActivity;

public class ContactsActivity extends AppCompatActivity {


    Button btnAbrirChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        btnAbrirChat = (Button) findViewById(R.id.btnAbrirChat);
        btnAbrirChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ContactsActivity.this, ChatActivity.class));
            }
        });
    }
}
