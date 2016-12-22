package com.upvhas.app.chaty.salas;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class SalasActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private DrawerLayout drawerLayout;
    private NavigationView salasNavigationView;
    private Toolbar salasToolbar;

    // Views
        // Header navigation drawer
    private TextView nameUser;
    private ImageView profileImageUser;

    // Google api client for sign out
    GoogleApiClient mGoogleApiClient;

    // Firebase Instances
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salas);

        setToolbar();

        // Initialize views
        drawerLayout = (DrawerLayout) findViewById(R.id.salas_drawer_layout);

        // Initialize Navigation view
        salasNavigationView = (NavigationView) findViewById(R.id.salas_navigation_view);
        if(salasNavigationView != null){
            View navView = salasNavigationView.getHeaderView(0);
            nameUser = (TextView) navView.findViewById(R.id.salas_drawer_name_TextView);
            profileImageUser = (ImageView) navView.findViewById(R.id.salas_drawer_profile_photo);
            setupDrawerContent(salasNavigationView);
        }



        // Gooogle Sign In Requisito for signOut()
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

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

        Intent intent = getIntent();
        if(intent != null){
            nameUser.setText(intent.getStringExtra("displayName"));
            Log.v("PHOTO_URL_GOOGLE",intent.getStringExtra("photoUrl"));
            Glide.with(SalasActivity.this)
                    .load(intent.getStringExtra("photoUrl"))
                    .into(profileImageUser);
        }

        // load fragment salas
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_salas_container,new SalasFragment())
                    .commit();
        }
    } // --> end onCreate

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

    private void signOut() {
        // Firebase sign out
        mFirebaseAuth.signOut();
        // google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {

            }
        });
    }

    /**
     * Handle click for navigation view
     * @param navigationView is the current in the activity layout
     */
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.salas_drawer_action_signout:
                                signOut();
                                return true;
                            case R.id.salas_drawer_action_crearChat:
                                    showCreateChatFragment();
                                return true;
                        }
                        String title = menuItem.getTitle().toString();
                        Toast.makeText(SalasActivity.this,title,Toast.LENGTH_LONG).show();
                        return true;
                    }
                }
        );
    }

    private void showCreateChatFragment(){
        drawerLayout.closeDrawer(GravityCompat.START);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_salas_container,new CreateChatFragment())
                .commit();
    }

    /**
     * Set toolbar as actionbar and enabled hamburguer icon click
     */
    private void setToolbar() {
        salasToolbar = (Toolbar) findViewById(R.id.salas_toolbar);
        setSupportActionBar(salasToolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.mipmap.ic_dehaze_black_24dp);
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.salas_menu_toolbar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
