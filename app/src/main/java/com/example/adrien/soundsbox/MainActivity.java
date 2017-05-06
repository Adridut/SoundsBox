package com.example.adrien.soundsbox;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements PadAdapter.ItemClickListener {

    public static final int ADD_REQUEST_CODE = 1;
    ArrayList<Pad> pads;
    RecyclerView rv;
    PadAdapter adapter;
    String mFileName;

    private MediaPlayer mPlayer = null;

    private static final String LOG_TAG = "AudioRecordTest";

    //TODO save sounds


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pads = new ArrayList<>();

        rv = (RecyclerView) findViewById(R.id.pads_list);

        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), rv, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        mFileName = pads.get(position).fileName;
                        if (pads.get(position).isPlaying) {
                            if (pads.get(position).mediaPlayer != null) {
                                pads.get(position).mediaPlayer.release();
                                pads.get(position).mediaPlayer = null;
                            }
                            pads.get(position).color = Color.parseColor("#3F51B5");
                            pads.get(position).isPlaying = false;
                        } else {
                            pads.get(position).mediaPlayer = new MediaPlayer();
                            try {
                                pads.get(position).mediaPlayer.setDataSource(mFileName);
                                pads.get(position).mediaPlayer.prepare();
                                pads.get(position).mediaPlayer.start();
                            } catch (IOException e) {
                                Log.e(LOG_TAG, "prepare() failed");
                            }
                            pads.get(position).color = Color.parseColor("#FF4081");
                            pads.get(position).isPlaying = true;
                        }
                        rv.setAdapter(adapter);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                        //TODO remove sound
                    }
                })
        );
        rv.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new PadAdapter(this, pads);
        adapter.setClickListener(this);
        rv.setAdapter(adapter);


        ImageButton add = (ImageButton) findViewById(R.id.add_button);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Pad pad : pads){
                    if (pad.mediaPlayer != null){
                        pad.mediaPlayer.release();
                        pad.mediaPlayer = null;
                        pad.color = Color.parseColor("#3F51B5");
                        rv.setAdapter(adapter);
                    }

                }
                Intent i = new Intent(getApplicationContext(), AddSound.class);
                startActivityForResult(i, ADD_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ADD_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String name = data.getStringExtra("name");
                String mfileName = data.getStringExtra("fileName");
                pads.add(new Pad(name, mfileName, false, Color.parseColor("#3F51B5"), mPlayer));
                adapter = new PadAdapter(this, pads);
                adapter.setClickListener(this);
                rv.setAdapter(adapter);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    @Override
    public void onItemClick(View view, int position) {

    }

}

