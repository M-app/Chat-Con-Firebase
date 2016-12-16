package com.upvhas.app.chaty.entities;

/**
 * Created by user on 15/12/2016.
 */

public class Sala {
    private String id;
    private String nombre;
    private String admin;
    private String imageUrl;
    private boolean isPublica;

    public Sala(){}

    public Sala(String id, String nombre, String admin, String imageUrl, boolean isPublica) {
        this.id = id;
        this.nombre = nombre;
        this.admin = admin;
        this.imageUrl = imageUrl;
        this.isPublica = isPublica;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isPublica() {
        return isPublica;
    }

    public void setPublica(boolean publica) {
        this.isPublica = publica;
    }
}
