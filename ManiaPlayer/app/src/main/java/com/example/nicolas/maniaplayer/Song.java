package com.example.nicolas.maniaplayer;

/**
 * Created by Nicolas on 30.03.2018.
 */

public class Song {
    private String titel;
    private String interpret;
    private int imageResourceId;
    private int audioResourceId;



  /*  public Song(String titel, String interpret, int imageResourceId, int audioResourceId) {
        this.titel = titel;
        this.interpret = interpret;
        this.imageResourceId = imageResourceId;
        this.audioResourceId = audioResourceId;

    }*/

    public Song(String titel, String interpret) {
        this.titel = titel;
        this.interpret = interpret;
    }

    public String getTitel() {
        return titel;
    }

    public String getInterpret() {
        return interpret;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public int getAudioResourceId() {
        return audioResourceId;
    }
}
