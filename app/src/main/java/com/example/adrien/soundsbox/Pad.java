package com.example.adrien.soundsbox;

/**
 * Created by adrien on 03/03/17.
 */

public class Pad {

    String name, fileName;
    boolean isPlaying;

    public Pad(String name, String fileName, boolean isPlaying){
        this.name = name;
        this.fileName = fileName;
        this.isPlaying = isPlaying;
    }
}
