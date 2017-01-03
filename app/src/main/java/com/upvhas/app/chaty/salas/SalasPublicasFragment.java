package com.upvhas.app.chaty.salas;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upvhas.app.chaty.R;
import com.upvhas.app.chaty.chat.ChatActivity;
import com.upvhas.app.chaty.chat.ChatFragment;
import com.upvhas.app.chaty.entities.Sala;

/**
 * A simple {@link Fragment} subclass.
 */
public class SalasPublicasFragment extends Fragment {

    private Toolbar salasPublicasToolbar;
    private RecyclerView salasPublicasRecycler;
    private FirebaseRecyclerAdapter<Sala,SalasPublicasViewHolder> mRecyclerAdapter;

    DatabaseReference mSalasPublicasRef;
    DatabaseReference mCurrentUserReference;
    String mCurrentUserEmail;

    public SalasPublicasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_salas_publicas, container, false);
        salasPublicasToolbar = (Toolbar) rootView.findViewById(R.id.salas_publicas_toolbar);
        salasPublicasRecycler = (RecyclerView) rootView.findViewById(R.id.salas_publicas_recyclerview);

        // setup toolbar
        ((AppCompatActivity) getActivity()).setSupportActionBar(salasPublicasToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Salas Públicas");

        // init firebase
        mSalasPublicasRef = FirebaseDatabase.getInstance().getReference().child("salas");
        mCurrentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail()
                .replaceAll("\\#|\\*|\\]|\\[|\\{|\\}\\\"|_s_|\\s+","");
        mCurrentUserEmail = mCurrentUserEmail.replaceAll("\\.","_");
        mCurrentUserReference = FirebaseDatabase.getInstance().getReference().child("users").child(mCurrentUserEmail);
        mRecyclerAdapter = new FirebaseRecyclerAdapter<Sala, SalasPublicasViewHolder>(
                Sala.class,
                R.layout.item_salas_publicas,
                SalasPublicasViewHolder.class,
                mSalasPublicasRef) {
            @Override
            protected void populateViewHolder(SalasPublicasViewHolder viewHolder, Sala model, int position) {
                viewHolder.setNombreSala(model.getNombre());
                viewHolder.setImagenSala(model.getImageUrl());
            }
        };

        salasPublicasRecycler.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), salasPublicasRecycler ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, final int position) {
                        String nombre = mRecyclerAdapter.getItem(position).getNombre();
                        String admin = mRecyclerAdapter.getItem(position).getAdmin();
                        final String nombreChat = admin + "-" + nombre;
                        mCurrentUserReference.child("salas")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(!dataSnapshot.hasChild(nombreChat)){
                                            mCurrentUserReference.child("salas")
                                                    .child(nombreChat)
                                                    .setValue(mRecyclerAdapter.getItem(position))
                                                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            goToChat(nombreChat);
                                                        }
                                                    });
                                        }else{
                                            goToChat(nombreChat);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {}
                                });
                    }
                    @Override public void onLongItemClick(View view, int position) {
                    }
                })
        );

        // setup recyclerview
        salasPublicasRecycler.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),
                layoutManager.getOrientation());
        salasPublicasRecycler.addItemDecoration(dividerItemDecoration);
        salasPublicasRecycler.setLayoutManager(layoutManager);
        salasPublicasRecycler.setAdapter(mRecyclerAdapter);

        return rootView;
    }

    private void goToChat(String nombreChat){
        Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
        chatIntent.putExtra(ChatFragment.RC_NOMBRE_CHAT,nombreChat);
        startActivity(chatIntent);
    }

}
