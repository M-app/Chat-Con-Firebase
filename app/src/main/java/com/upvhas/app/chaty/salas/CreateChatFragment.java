package com.upvhas.app.chaty.salas;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.upvhas.app.chaty.entities.User;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateChatFragment extends Fragment implements View.OnClickListener{

    private static final int REQUEST_IMAGE_GET = 1;

    private EditText nombreChat;
    private ImageView imagenChat;
    private Button btnCambiarImagen;
    private Button btnCrearChat;
    private CheckBox checkEsPrivada;

    FirebaseStorage mFirebaseStorage;
    StorageReference mChatImagesStorageReference;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mSalasRef;
    FirebaseUser mUser;
    DatabaseReference mCurrentUserReference;

    private Sala mSala;
    private HashMap<String,Boolean> mSalasExistentes;
    private String mNombreImagen;

    public CreateChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Init fireabase
        mFirebaseStorage = FirebaseStorage.getInstance();
        mChatImagesStorageReference = mFirebaseStorage.getReference().child("salas_images");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mSalasRef = mFirebaseDatabase.getReference().child("salas");
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentUserReference = mFirebaseDatabase.getReference()
                .child("users")
                .child(mUser.getEmail().replace('.','_'));

        mCurrentUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user.getSalas() != null){ // el usuario ya tiene salas
                    mSalasExistentes = (HashMap<String, Boolean>) user.getSalas();
                }else{
                    mSalasExistentes = null;
                }
            }
            @Override public void onCancelled(DatabaseError databaseError) {
            }
        });

        // initi sala object
        mSala = new Sala();
        mSala.setAdmin(mUser.getEmail().replace('.','_'));

        // set sala image
        setChatImageFromDensity();

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_create_chat, container, false);
        nombreChat = (EditText) rootView.findViewById(R.id.nuevo_chat_nombreChat);
        imagenChat = (ImageView) rootView.findViewById(R.id.nuevo_chat_ImageView);
        btnCambiarImagen = (Button) rootView.findViewById(R.id.nuevo_chat_cambiarImageView);
        btnCambiarImagen.setOnClickListener(this);
        btnCrearChat = (Button) rootView.findViewById(R.id.nuevo_chat_listo_Button);
        btnCrearChat.setOnClickListener(this);
        checkEsPrivada = (CheckBox) rootView.findViewById(R.id.nuevo_chat_check_esPrivada);


        nombreChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().trim().length() > 0){
                    btnCambiarImagen.setEnabled(true);
                    btnCrearChat.setEnabled(true);
                }else{
                    btnCambiarImagen.setEnabled(false);
                    btnCrearChat.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(final Editable editable) {
                mSala.setNombre(nombreChat.getText().toString()/*.replaceAll("(?s).|\\s+","")*/);
                mSala.setId(mUser.getEmail().replace('.','_') + "_" + mSala.getNombre());
                mNombreImagen = mSala.getId() + "_";
                Toast.makeText(getActivity(),mSala.getId(),Toast.LENGTH_LONG).show();
                boolean salaExiste = mSalasExistentes.containsKey(mSala.getId());
                if(salaExiste){
                    nombreChat.setError("Sala ya existe!");
                    btnCrearChat.setEnabled(false);
                }else{
                    btnCrearChat.setEnabled(true);
                }
            } // verificar si el nombre de la sala ya existe para este usuario
        });
        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.nuevo_chat_cambiarImageView:
                    selectImage();
                break;
            case R.id.nuevo_chat_listo_Button:
                    publicarSala();
                break;
        }

    }

    private void publicarSala(){
        mSala.setPublica(checkEsPrivada.isSelected());
        // firebase database new sala
        mSalasRef.child(mSala.getId()).setValue(mSala);
        // add sala to user object database
        if(mSalasExistentes == null){
            mSalasExistentes = new HashMap<>();
        }
        mSalasExistentes.put(mSala.getId(),false);
        Map<String,Object> map = new HashMap<>();
        map.put("salas",mSalasExistentes);
        mCurrentUserReference.updateChildren(map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

            }
        });
    }

    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }

    private void setChatImageFromDensity(){
        switch (getResources().getDisplayMetrics().densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                mSala.setImageUrl("https://firebasestorage.googleapis.com/v0/b/chaty-ef037.appspot.com/o/util_images%2FnewChatEmptyImages%2Fmdpi.png?alt=media&token=e5fd1e12-8694-4eff-af23-2f9a96ab377e");
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                mSala.setImageUrl("https://firebasestorage.googleapis.com/v0/b/chaty-ef037.appspot.com/o/util_images%2FnewChatEmptyImages%2Fmdpi.png?alt=media&token=e5fd1e12-8694-4eff-af23-2f9a96ab377e");
                break;
            case DisplayMetrics.DENSITY_HIGH:
                mSala.setImageUrl("https://firebasestorage.googleapis.com/v0/b/chaty-ef037.appspot.com/o/util_images%2FnewChatEmptyImages%2Fhdpi.png?alt=media&token=f9742148-6bca-4e9c-baa2-86a9afd8ed55");
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                mSala.setImageUrl("https://firebasestorage.googleapis.com/v0/b/chaty-ef037.appspot.com/o/util_images%2FnewChatEmptyImages%2Fxhdpi.png?alt=media&token=d0912c25-d586-4e0a-9e84-b3e41da28120");
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                mSala.setImageUrl("https://firebasestorage.googleapis.com/v0/b/chaty-ef037.appspot.com/o/util_images%2FnewChatEmptyImages%2Fxxhdpi.png?alt=media&token=25b1d790-440c-4891-b891-fd9579de444f");
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                mSala.setImageUrl("https://firebasestorage.googleapis.com/v0/b/chaty-ef037.appspot.com/o/util_images%2FnewChatEmptyImages%2Fxxxhdpi.png?alt=media&token=aaa2d250-ca21-41b2-b988-287b1ff5f1e3");
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            btnCrearChat.setEnabled(false);
            nombreChat.setEnabled(false);
            Uri fullPhotoUri = data.getData();
            String nameImage =  "nombreArbitrario";/*mNombreImagen += fullPhotoUri.getLastPathSegment();*/
            StorageReference imageRef = mChatImagesStorageReference.child(nameImage);
            imageRef.putFile(fullPhotoUri).addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Glide.with(getActivity())
                            .load(taskSnapshot.getDownloadUrl())
                            .into(imagenChat);
                    mSala.setImageUrl(taskSnapshot.getDownloadUrl().toString());
                    btnCrearChat.setEnabled(true);
                }
            });
        }
    }
}
