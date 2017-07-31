package com.example.adrien.soundsbox;

import android.app.Activity;

import java.io.File;

/**
 * Created by adrien on 31/07/17.
 */

public class Utils extends Activity{

    public int checkName(String name, File soundsDir){
        if (name.isEmpty()){
            return 0;
        }
        else if (!checkOverwrittedName(name, soundsDir)){
            return -1;
        } else  {
            return 1;
        }
    }

    //check if the name is already used for another sound
    public boolean checkOverwrittedName (String name, File soundsDir){
        boolean isOverwritted = false;

        File[] soundsFiles = soundsDir.listFiles();

        for (File f : soundsFiles) {
            String fName = f.getName();
            if (fName.endsWith(".mp3") && fName.substring(0, fName.length() - 4).equals(name)) {
                isOverwritted = true;
            }
        }
        if (!isOverwritted){
            return true;
        } else {
            return false;
        }
    }
}
