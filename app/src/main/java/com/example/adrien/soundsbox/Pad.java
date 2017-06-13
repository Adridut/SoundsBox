package com.example.adrien.soundsbox;

import android.media.MediaPlayer;

import java.io.File;

/**
 * Created by adrien on 03/03/17.
 */

public class Pad {

    String name, fileName;
    boolean isPlaying;
    int color;
    MediaPlayer mediaPlayer;
    File file;


    public Pad(String name, String fileName, boolean isPlaying, int color, MediaPlayer mediaPlayer, File file){
        this.name = name;
        this.fileName = fileName;
        this.isPlaying = isPlaying;
        this.color = color;
        this.mediaPlayer = mediaPlayer;
        this.file = file;
    }
}
