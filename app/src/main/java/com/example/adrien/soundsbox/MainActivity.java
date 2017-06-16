package com.example.adrien.soundsbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements PadAdapter.ItemClickListener {

    public static final int ADD_REQUEST_CODE = 1;
    ArrayList<Pad> pads;
    RecyclerView rv;
    PadAdapter adapter;
    String mFileName;
    int count = 0;
    File soundsDir;

    private MediaPlayer mPlayer = null;

    private static final String LOG_TAG = "AudioRecordTest";

    //TODO change the logo

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // retrieving files
        soundsDir = new File(getFilesDir().getAbsolutePath());
        File[] soundsFiles = soundsDir.listFiles();

        pads = new ArrayList<>();

        for (File f : soundsFiles) {
            String name = f.getName();
            if (name.endsWith(".3gp")) {
                pads.add(new Pad(name.substring(0, name.length() - 5), getFilesDir().getAbsolutePath() + "/" + name, false, Color.parseColor("#512DA8"), mPlayer, f));
                count = Integer.parseInt(name.substring(name.length() - 5, name.length() -4)) + 1;
            }
        }


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
                            pads.get(position).color = getResources().getColor(R.color.colorPrimary);
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
                            pads.get(position).color = getResources().getColor(R.color.colorAccent);
                            pads.get(position).isPlaying = true;
                        }
                        rv.setAdapter(adapter);
                    }

                    @Override
                    public void onLongItemClick(View view, final int position) {
                        if (pads.get(position).isPlaying) {
                            if (pads.get(position).mediaPlayer != null) {
                                pads.get(position).mediaPlayer.release();
                                pads.get(position).mediaPlayer = null;
                            }
                            pads.get(position).color = getResources().getColor(R.color.colorPrimary);
                            pads.get(position).isPlaying = false;
                            rv.setAdapter(adapter);
                        }
                        //TODO add a list with play and modifie options
                        AlertDialog.Builder builder;
                        builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Delete " + pads.get(position).name)
                                .setMessage("Are you sure you want to delete this sound ?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        pads.get(position).file.delete();
                                        pads.remove(position);
                                        rv.setAdapter(adapter);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .show();
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
                for (Pad pad : pads) {
                    if (pad.mediaPlayer != null) {
                        pad.mediaPlayer.release();
                        pad.mediaPlayer = null;
                        pad.color = getResources().getColor(R.color.colorPrimary);
                        rv.setAdapter(adapter);
                    }

                }
                Intent i = new Intent(getApplicationContext(), AddSound.class);
                i.putExtra("Count", count);
                count++;
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
                pads.add(new Pad(name, mfileName, false, getResources().getColor(R.color.colorPrimary), mPlayer, new File(mfileName)));
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

