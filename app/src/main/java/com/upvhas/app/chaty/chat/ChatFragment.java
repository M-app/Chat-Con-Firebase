package com.upvhas.app.chaty.chat;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.upvhas.app.chaty.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    public static final String ANONYMOUS = "anonymous";

    RecyclerView mRecyclerMessages;
    ImageButton mPickImageButton;
    EditText mWriteMessageEditText;
    Button mSendButton;

    private ChatMessageAdapter adapter;

    private String mUserName;

    public ChatFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mUserName = ANONYMOUS;
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        mRecyclerMessages = (RecyclerView) rootView.findViewById(R.id.chat_messages_RecyclerView);
        mPickImageButton = (ImageButton) rootView.findViewById(R.id.photoPickerButton);
        mWriteMessageEditText = (EditText) rootView.findViewById(R.id.messageEditText);
        mSendButton = (Button) rootView.findViewById(R.id.sendButton);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerMessages.setHasFixedSize(false);
        mRecyclerMessages.setLayoutManager(layoutManager);

        adapter = new ChatMessageAdapter(getActivity());
        mRecyclerMessages.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mRecyclerMessages.smoothScrollToPosition(adapter.getItemCount());
            }
        });

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

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoatMessage message =
                        new GoatMessage(mUserName,mWriteMessageEditText.getText().toString(),null);
                adapter.addMessage(message);
                mWriteMessageEditText.setText("");
            }
        });

        return rootView;
    }

}
