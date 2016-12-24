package com.upvhas.app.chaty.salas;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.upvhas.app.chaty.R;

/**
 * Created by user on 15/12/2016.
 */

public class SalasViewHolder extends RecyclerView.ViewHolder {

    private ImageView imagenSala;
    private TextView nombreSala;
    private View nuevoMensaje;

    public SalasViewHolder(View itemView) {
        super(itemView);
        imagenSala = (ImageView) itemView.findViewById(R.id.photo_sala_ImageView);
        nombreSala = (TextView) itemView.findViewById(R.id.name_sala_TextView);
        nuevoMensaje = (View) itemView.findViewById(R.id.nuevo_mensaje_view);
    }

    public void setImagenSala(String url) {
        Glide.with(imagenSala.getContext())
                .load(url)
                .into(imagenSala);
    }

    public void setNombreSala(String nombre) {
        nombreSala.setText(nombre);
    }

    public void setNuevoMensaje(Boolean nuevoMsg) {
        if (nuevoMsg){
            nuevoMensaje.setVisibility(View.VISIBLE);
        }else{
            nuevoMensaje.setVisibility(View.GONE);
        }
    }
}
