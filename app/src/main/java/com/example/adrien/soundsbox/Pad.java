package com.example.adrien.soundsbox;

/**
 * Created by adrien on 03/03/17.
 */

public class Pad {

    String name, fileName;
    boolean isPlaying;
    int color;


    public Pad(String name, String fileName, boolean isPlaying, int color){
        this.name = name;
        this.fileName = fileName;
        this.isPlaying = isPlaying;
        this.color = color;
    }
}
