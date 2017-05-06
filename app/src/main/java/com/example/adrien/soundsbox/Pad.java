package com.example.adrien.soundsbox;

import android.media.MediaPlayer;

/**
 * Created by adrien on 03/03/17.
 */

public class Pad {

    String name, fileName;
    boolean isPlaying;
    int color;
    MediaPlayer mediaPlayer;


    public Pad(String name, String fileName, boolean isPlaying, int color, MediaPlayer mediaPlayer){
        this.name = name;
        this.fileName = fileName;
        this.isPlaying = isPlaying;
        this.color = color;
        this.mediaPlayer = mediaPlayer;
    }
}
