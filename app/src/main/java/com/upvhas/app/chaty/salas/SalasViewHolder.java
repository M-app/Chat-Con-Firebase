package com.upvhas.app.chaty.salas;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.upvhas.app.chaty.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by user on 15/12/2016.
 */

public class SalasViewHolder extends RecyclerView.ViewHolder {

    private CircleImageView imagenSala;
    private TextView nombreSala;

    public SalasViewHolder(View itemView) {
        super(itemView);
        imagenSala = (CircleImageView) itemView.findViewById(R.id.photo_sala_ImageView);
        nombreSala = (TextView) itemView.findViewById(R.id.name_sala_TextView);
    }

    public void setImagenSala(String url) {
        Glide.with(imagenSala.getContext())
                .using(new FirebaseImageLoader())
                .load(FirebaseStorage.getInstance().getReferenceFromUrl(url))
                .dontAnimate()
                .into(imagenSala);
    }
    public void setNombreSala(String nombre) {
        nombreSala.setText(nombre);
    }

}
