package com.upvhas.app.chaty.entities;

import java.util.List;

/**
 * Created by user on 20/12/2016.
 */

public class User {

    private String email;
    private String nombre;
    private String urlImagenPerfil;
    private List<Sala> salas;

    public User() {
    }

    public User(String email, String nombre, String urlImagenPerfil, List<Sala> salas) {
        this.email = email;
        this.nombre = nombre;
        this.urlImagenPerfil = urlImagenPerfil;
        this.salas = salas;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUrlImagenPerfil() {
        return urlImagenPerfil;
    }

    public void setUrlImagenPerfil(String urlImagenPerfil) {
        this.urlImagenPerfil = urlImagenPerfil;
    }

    public List<Sala> getSalas() {
        return salas;
    }

    public void setSalas(List<Sala> salas) {
        this.salas = salas;
    }
}
