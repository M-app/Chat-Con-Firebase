package com.upvhas.app.chaty.chat;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.upvhas.app.chaty.R;
import com.upvhas.app.chaty.entities.Sala;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    public static final String RC_NOMBRE_CHAT = "NOMBRE_CHAT_REF";
    public static final String RC_CURRET_SALA_KEY = "CURRENT_SALA_KEY";
    public static final String RC_ADMIN_SALA = "CURRENT_SALA_ADMIN";
    public static final int RC_PHOTO_PICKER = 222;

    RecyclerView mRecyclerMessages;
    ImageButton mPickImageButton;
    EditText mWriteMessageEditText;
    ImageButton mSendButton;
    Toolbar chatToolbar;

    private String mNombreChat;

    private String mCurrentSalaKey;
    DatabaseReference mCurrentChatReference;
    StorageReference mChatsImagesReference;
    FirebaseRecyclerAdapter<Message,ChatMessageViewHolder> mRecyclerAdapter;
    LinearLayoutManager mLayoutManager;


    private String mCorreoInvitado;
    private String mAdminCurrentChat;

    private String mUserName;

    public ChatFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName().replaceAll("\\s+","");
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        // Init nombre chat
        Intent chatIntent = getActivity().getIntent();
        if(chatIntent != null){
            if(chatIntent.hasExtra(RC_NOMBRE_CHAT)){
                mNombreChat = chatIntent.getStringExtra(RC_NOMBRE_CHAT);
            }
            if (chatIntent.hasExtra(RC_CURRET_SALA_KEY)){
                mCurrentSalaKey = chatIntent.getStringExtra(RC_CURRET_SALA_KEY);
            }
            if (chatIntent.hasExtra(RC_ADMIN_SALA)){
                mAdminCurrentChat = chatIntent.getStringExtra(RC_ADMIN_SALA);
            }
        }
        // Initialize views
        mRecyclerMessages = (RecyclerView) rootView.findViewById(R.id.chat_messages_RecyclerView);
        mPickImageButton = (ImageButton) rootView.findViewById(R.id.photoPickerButton);
        mWriteMessageEditText = (EditText) rootView.findViewById(R.id.messageEditText);
        mSendButton = (ImageButton) rootView.findViewById(R.id.sendButton);
        chatToolbar = (Toolbar) rootView.findViewById(R.id.chat_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(chatToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mNombreChat.substring(0,mNombreChat.indexOf("-")));

        // Init firebase references
        mCurrentChatReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("chats")
                .child(mNombreChat);
        mChatsImagesReference = FirebaseStorage.getInstance().getReference().child("chat_images");

        // layout manager recyclerview
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setStackFromEnd(true);
        mRecyclerMessages.setHasFixedSize(false);
        mRecyclerMessages.setLayoutManager(mLayoutManager);

        // Initialize mRecyclerAdapter recyclerview
        mRecyclerAdapter = new FirebaseRecyclerAdapter<Message, ChatMessageViewHolder>(
                Message.class,
                R.layout.item_chat,
                ChatMessageViewHolder.class,
                mCurrentChatReference) {
            @Override
            protected void populateViewHolder(ChatMessageViewHolder viewHolder, Message model, int position) {
                viewHolder.setAuthorTextview(model.getAutor());
                viewHolder.setTextMessageTextView(model.getTextMessage());
                viewHolder.setImageMessageImageView(model.getPhotoUrl());
            }
        };


        mRecyclerMessages.setAdapter(mRecyclerAdapter);

        // scroll to last position when a message is inserted
        mRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mRecyclerAdapter.getItemCount();
                int lastVisiblePosition =
                        mLayoutManager.findLastCompletelyVisibleItemPosition();
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
                intent.setType("image/*");
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
    public void onDestroyView() {
        super.onDestroyView();
        mRecyclerAdapter.cleanup();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.chat_toolbar,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.chat_action_invite:
                showInviteToChatDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showInviteToChatDialog(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View dialogView = inflater.inflate(R.layout.dialog_invite_tochat, null);
        final EditText correo = (EditText) dialogView.findViewById(R.id.invite_chat_email);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Agregar a chat")
                .setView(dialogView)
                .setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mCorreoInvitado = correo.getText().toString().replaceAll("\\#|\\*|\\]|\\[|\\|\\{|\\}\\\"","");
                        mCorreoInvitado = mCorreoInvitado.replaceAll("\\.","_");
                        if (emailExist(mCorreoInvitado)){
                            FirebaseDatabase.getInstance().getReference().child("salas")
                                    .child(mAdminCurrentChat)
                                    .child(mCurrentSalaKey)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Sala sala = dataSnapshot.getValue(Sala.class);
                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("salas")
                                                    .child(mCorreoInvitado)
                                                    .push()
                                                    .setValue(sala)
                                                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                }
                                            });
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }else {
                            correo.setError("Email aún no existe");
                        }
                        Toast.makeText(getActivity(),mCorreoInvitado,Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create();
        dialog.show();

    }

    private boolean emailExist(String email){
        DatabaseReference emailRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(email);
        return emailRef != null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            final StorageReference photoRef
                    = mChatsImagesReference.child(mUserName + selectedImageUri.getLastPathSegment());
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
