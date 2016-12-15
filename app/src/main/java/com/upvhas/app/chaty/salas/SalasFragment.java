package com.upvhas.app.chaty.salas;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.upvhas.app.chaty.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SalasFragment extends Fragment {


    public SalasFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_salas, container, false);
    }

}
