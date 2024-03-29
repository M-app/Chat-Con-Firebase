package com.upvhas.app.chaty.salas;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
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
import com.upvhas.app.chaty.login.LoginActivity;

public class SalasActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private static final int RC_CHANGE_PROFILE_PHOTO = 111;

    private DrawerLayout drawerLayout;
    private NavigationView salasNavigationView;
    private Toolbar salasToolbar;

    // Views
        // Header navigation drawer
    private TextView nameUser;
    private TextView userName;
    private ImageView profileImageUser;
    private ImageButton changeProfilePhotoButton;

    // Google api client for sign out
    GoogleApiClient mGoogleApiClient;

    // Firebase Instances
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mStateListener;
    DatabaseReference mCurrentUserReference;

    private String mEmailUser;

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
            userName = (TextView) navView.findViewById(R.id.salas_drawer_username_TextView);
            profileImageUser = (ImageView) navView.findViewById(R.id.salas_drawer_profile_photo);
            changeProfilePhotoButton = (ImageButton) navView.findViewById(R.id.salas_drawer_change_profilePhoto_ImageButton);
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
        mEmailUser = mFirebaseAuth.getCurrentUser().getEmail().replaceAll("\\#|\\*|\\]|\\[|\\{|\\}\\\"|\\s+","");
        mEmailUser = mEmailUser.replaceAll("\\.","_");
        mCurrentUserReference = FirebaseDatabase.getInstance().getReference().child("users").child(mEmailUser);

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

        setUserData();

        // load fragment salas
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_salas_container,new SalasFragment())
                    .commit();
        }

        changeProfilePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
    } // --> end onCreate

    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, false);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(intent, "Continuar acción utilizando"), RC_CHANGE_PROFILE_PHOTO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_CHANGE_PROFILE_PHOTO){
            if(resultCode == RESULT_OK){
                Uri fullPhotoUri = data.getData();
                String nameImage = mEmailUser + "-" + "profile";
                StorageReference imageRef = FirebaseStorage.getInstance()
                        .getReference().child("profile_images").child(nameImage);
                imageRef.putFile(fullPhotoUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mCurrentUserReference.child("urlImagenPerfil").setValue(taskSnapshot.getDownloadUrl().toString())
                                .addOnSuccessListener(SalasActivity.this, new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        setUserData();
                                    }
                                });
                    }
                });
            }
        }
    }

    private void setUserData(){
        nameUser.setText(mFirebaseAuth.getCurrentUser().getDisplayName());
        userName.setText(mFirebaseAuth.getCurrentUser().getEmail());
        mCurrentUserReference.child("urlImagenPerfil")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Glide.with(SalasActivity.this)
                                .load(dataSnapshot.getValue(String.class))
                                .into(profileImageUser);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
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
                                    startCreateChatActivity();
                                return true;
                            case R.id.salas_drawer_action_unirseChat:
                                startPublicSalasActivity();
                                return true;
                        }
                        return true;
                    }
                }
        );
    }

    private void startPublicSalasActivity(){
        drawerLayout.closeDrawer(GravityCompat.START);
        startActivity(new Intent(this,SalasPublicasActivity.class));
    }

    private void startCreateChatActivity(){
        drawerLayout.closeDrawer(GravityCompat.START);
        startActivity(new Intent(this,CreateChatActivity.class));
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
