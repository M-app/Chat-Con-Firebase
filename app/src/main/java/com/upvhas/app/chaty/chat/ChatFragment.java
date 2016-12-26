package com.upvhas.app.chaty.chat;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upvhas.app.chaty.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    public static final String ANONYMOUS = "anonymous";

    public static final String RC_NOMBRE_CHAT = "NOMBRE_CHAT_REF";

    RecyclerView mRecyclerMessages;
    ImageButton mPickImageButton;
    EditText mWriteMessageEditText;
    ImageButton mSendButton;

    private String mNombreChat;

    DatabaseReference mCurrentChatReference;
    FirebaseRecyclerAdapter<Message,ChatMessageViewHolder> mAdapter;

    private String mUserName;

    public ChatFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName().replaceAll("\\s+","");
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        // Init nombre chat
        Intent chatIntent = getActivity().getIntent();
        if(chatIntent != null){
            mNombreChat = chatIntent.getStringExtra(RC_NOMBRE_CHAT);
        }else {
            mNombreChat = "anonimo";
        }

        // Initialize views
        mRecyclerMessages = (RecyclerView) rootView.findViewById(R.id.chat_messages_RecyclerView);
        mPickImageButton = (ImageButton) rootView.findViewById(R.id.photoPickerButton);
        mWriteMessageEditText = (EditText) rootView.findViewById(R.id.messageEditText);
        mSendButton = (ImageButton) rootView.findViewById(R.id.sendButton);

        // Init firebase references
        mCurrentChatReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("chats")
                .child(mNombreChat);

        // layout manager recyclerview
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerMessages.setHasFixedSize(false);
        mRecyclerMessages.setLayoutManager(layoutManager);

        // Initialize mAdapter recyclerview
        mAdapter = new FirebaseRecyclerAdapter<Message,ChatMessageViewHolder>(
                Message.class,
                R.layout.item_chat,
                ChatMessageViewHolder.class,
                mCurrentChatReference) {
            @Override
            protected void populateViewHolder(ChatMessageViewHolder viewHolder, Message model, int position) {
                viewHolder.bind(model);
            }
        };

        mRecyclerMessages.setAdapter(mAdapter);


        // Set Enable sendButton if EditText contains some text
        mWriteMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0){
                    mSendButton.setEnabled(true);
                }else{
                    mSendButton.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // action send button
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message =
                        new Message(mUserName,mWriteMessageEditText.getText().toString(),"hola");
                mCurrentChatReference.push().setValue(message);
                mWriteMessageEditText.setText("");
            }
        });

        return rootView;
    }

}
