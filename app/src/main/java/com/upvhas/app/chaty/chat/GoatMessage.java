package com.upvhas.app.chaty.chat;

/**
 * Created by user on 9/12/2016.
 */

public class GoatMessage {

    private String autor;
    private String textMessage;
    private String photoUrl;

    public GoatMessage(String autor, String textMessage, String photoUrl) {
        this.autor = autor;
        this.textMessage = textMessage;
        this.photoUrl = photoUrl;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
