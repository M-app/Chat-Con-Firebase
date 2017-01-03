package com.upvhas.app.chaty.salas;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.upvhas.app.chaty.R;

public class SalasPublicasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salas_publicas);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_salas_publicas_container,new SalasPublicasFragment())
                .commit();
    }
}
