package com.example.adrien.soundsbox;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;


/**
 * Created by adrien on 03/03/17.
 */

public class AddSound extends Activity {

    ImageButton record, save;
    String mFileName;
    EditText name;
    TextView recordText;
    boolean isRecordActive = false;

    private MediaRecorder mRecorder = null;

    private static final String LOG_TAG = "AudioRecordTest";

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};


    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_sound_layout);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        name = (EditText) findViewById(R.id.sound_name);
        save = (ImageButton) findViewById(R.id.save_button);
        record = (ImageButton) findViewById(R.id.recordButton);
        recordText = (TextView) findViewById(R.id.record_text);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("name", String.valueOf(name.getText()));
                returnIntent.putExtra("fileName", mFileName);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        //TODO onLongClick
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecordActive) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        record.setBackground(getResources().getDrawable(R.drawable.rounded_primary_button));
                    }
                    recordText.setText("Recording...");
                    startRecording();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        record.setBackground(getResources().getDrawable(R.drawable.rounded_accent_button));
                        recordText.setText("Save your record or click here again to erase it and start a new one");
                        stopRecording();
                        if (save.getVisibility() == View.GONE){
                            save.setVisibility(View.VISIBLE);
                        }
                    }
                }
                isRecordActive = !isRecordActive;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) finish();

    }

    private void startRecording() {

        int count = getIntent().getIntExtra("Count", 0);

        mFileName = getFilesDir().getAbsolutePath() + "/" + String.valueOf(name.getText()) + String.valueOf(count) + ".3gp";

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }
}
