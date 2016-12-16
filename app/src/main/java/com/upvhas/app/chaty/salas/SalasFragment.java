package com.upvhas.app.chaty.salas;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.upvhas.app.chaty.R;
import com.upvhas.app.chaty.login.LoginActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class SalasFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener{

    private TextView displayNameTextView;
    private ImageView profileImageView;
    private android.support.v7.widget.Toolbar salasToolbar;

    // Google api client for sign out
    GoogleApiClient mGoogleApiClient;

    // Firebase Instances
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mStateListener;

    public SalasFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Gooogle Sign In Requisito for signOut()
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

         mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_salas, container, false);
        // initialize views
        displayNameTextView = (TextView) rootView.findViewById(R.id.displayNameTextView);
        profileImageView = (ImageView) rootView.findViewById(R.id.profileImageView);
        salasToolbar = (android.support.v7.widget.Toolbar) rootView.findViewById(R.id.salas_toolbar);

        // set the toolbar as action bar
        ((AppCompatActivity) getActivity()).setSupportActionBar(salasToolbar);

        // Firebase Initialize
        mFirebaseAuth = FirebaseAuth.getInstance();

        // Initialize AuthStateListener
        mStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){// user signed in

                }else{ // user signed out
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            }
        };

        Intent intent = getActivity().getIntent();
        if(intent != null){
            displayNameTextView.setText(intent.getStringExtra("displayName"));
             Log.v("PHOTO_URL_GOOGLE",intent.getStringExtra("photoUrl"));
             Glide.with(profileImageView.getContext())
                    .load(intent.getStringExtra("photoUrl"))
                    .into(profileImageView);
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mStateListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mStateListener != null){
            mFirebaseAuth.removeAuthStateListener(mStateListener);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_salas,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_signOut:
                signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        // Firebase sign out
        mFirebaseAuth.signOut();
        // google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Toast.makeText(getActivity(),"Log out",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getActivity(),"Google play services error!",Toast.LENGTH_LONG).show();
    }

}
