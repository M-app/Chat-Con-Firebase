package com.upvhas.app.chaty.salas;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.upvhas.app.chaty.R;
import com.upvhas.app.chaty.login.LoginActivity;

public class SalasActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{


    Button btnAbrirChat;

    // Google api client for sign out
    GoogleApiClient mGoogleApiClient;

    // Firebase Instances
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        btnAbrirChat = (Button) findViewById(R.id.btnAbrirChat);
        btnAbrirChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(SalasActivity.this, ChatActivity.class));
                signOut();
            }
        });

        // Firebase Initialize
        mFirebaseAuth = FirebaseAuth.getInstance();

        // Initialize AuthStateListener
        mStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){// user signed in

                }else{ // user signed out
                    startActivity(new Intent(SalasActivity.this, LoginActivity.class));
                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    } // --> end onCreate

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mStateListener != null){
            mFirebaseAuth.removeAuthStateListener(mStateListener);
        }
    }

    private void signOut() {
        // Firebase sign out
        mFirebaseAuth.signOut();
        // google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Toast.makeText(SalasActivity.this,"Log out",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(SalasActivity.this,"Google play services error!",Toast.LENGTH_LONG).show();
    }
}
