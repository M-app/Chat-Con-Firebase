package com.upvhas.app.chaty.chat;


import android.content.Intent;
import android.net.Uri;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.upvhas.app.chaty.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    public static final String ANONYMOUS = "anonymous";

    public static final String RC_NOMBRE_CHAT = "NOMBRE_CHAT_REF";
    public static final int RC_PHOTO_PICKER = 222;

    RecyclerView mRecyclerMessages;
    ImageButton mPickImageButton;
    EditText mWriteMessageEditText;
    ImageButton mSendButton;

    private String mNombreChat;

    DatabaseReference mCurrentChatReference;
    FirebaseRecyclerAdapter<Message,ChatMessageViewHolder> mAdapter;
    StorageReference mChatsImagesReference;

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
        mChatsImagesReference = FirebaseStorage.getInstance().getReference().child("chat_images");

        // layout manager recyclerview
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
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

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mAdapter.getItemCount();
                int lastVisiblePosition =
                       layoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mRecyclerMessages.scrollToPosition(positionStart);
                }
            }
        });


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

        // action send image
        mPickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Continuar acción utilizando"), RC_PHOTO_PICKER);
            }
        });

        // action send button
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message =
                        new Message(mUserName,mWriteMessageEditText.getText().toString(),null);
                mCurrentChatReference.push().setValue(message);
                mWriteMessageEditText.setText("");
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            StorageReference photoRef
                    = mChatsImagesReference.child(selectedImageUri.getLastPathSegment());
            photoRef.putFile(selectedImageUri).addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Message message = new Message(mUserName,null,taskSnapshot.getDownloadUrl().toString());
                    mCurrentChatReference.push().setValue(message);
                }
            });
        }
    }
}
