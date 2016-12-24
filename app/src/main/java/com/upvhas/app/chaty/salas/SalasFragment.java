package com.upvhas.app.chaty.salas;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upvhas.app.chaty.R;
import com.upvhas.app.chaty.entities.Sala;

/**
 * A simple {@link Fragment} subclass.
 */
public class SalasFragment extends Fragment{

    RecyclerView mSalasRecyclerView;
    FirebaseRecyclerAdapter<Sala,SalasViewHolder> mRecyclerAdapter;
    DatabaseReference mSalasReference;
    FirebaseUser mUser;

    public SalasFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_salas, container, false);
        mSalasRecyclerView = (RecyclerView) rootView.findViewById(R.id.salas_recyclerview);
        mSalasRecyclerView.setHasFixedSize(false);
        mSalasRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // firebase init
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mSalasReference= FirebaseDatabase.getInstance().getReference()
                .child("salas")
                .child(mUser.getEmail().replace('.','_'));


        mRecyclerAdapter = new FirebaseRecyclerAdapter<Sala, SalasViewHolder>(Sala.class,
                R.layout.item_salas,
                SalasViewHolder.class,mSalasReference) {
            @Override
            protected void populateViewHolder(SalasViewHolder viewHolder, Sala model, int position) {
                viewHolder.setImagenSala(model.getImageUrl());
                viewHolder.setNombreSala(model.getNombre());
                viewHolder.setNuevoMensaje(false);
            }
        };

        mSalasRecyclerView.setAdapter(mRecyclerAdapter);

        return rootView;
    }


}
