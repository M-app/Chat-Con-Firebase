package com.upvhas.app.chaty.salas;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.upvhas.app.chaty.R;

/**
 * Created by user on 2/01/2017.
 */

public class SalasPublicasViewHolder extends RecyclerView.ViewHolder {

    private ImageView imagenSala;
    private TextView nombreSala;

    public SalasPublicasViewHolder(View itemView) {
        super(itemView);
        imagenSala = (ImageView) itemView.findViewById(R.id.item_salas_publicas_imagen);
        nombreSala = (TextView) itemView.findViewById(R.id.item_salas_publicas_nombreSala);
    }

    public void setImagenSala(String url){
        Glide.with(imagenSala.getContext())
                .using(new FirebaseImageLoader())
                .load(FirebaseStorage.getInstance().getReferenceFromUrl(url))
                .into(imagenSala);
    }

    public void setNombreSala(String nombre){
        nombreSala.setText(nombre);
    }
}
