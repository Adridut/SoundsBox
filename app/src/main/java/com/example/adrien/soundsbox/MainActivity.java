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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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
    File soundsDir;

    private MediaPlayer mPlayer = null;

    private static final String LOG_TAG = "AudioRecordTest";

    //TODO MINOR change the logo

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
                pads.add(new Pad(name.substring(0, name.length() - 4), getFilesDir().getAbsolutePath() + "/" + name, false, Color.parseColor("#512DA8"), mPlayer, f));
            }
        }

        rv = (RecyclerView) findViewById(R.id.pads_list);

        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), rv, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Pad p = pads.get(position);
                        if (p.isPlaying) {
                            if (p.mediaPlayer != null) {
                                p.mediaPlayer.release();
                                p.mediaPlayer = null;
                            }
                            p.color = getResources().getColor(R.color.colorPrimary);
                            p.isPlaying = false;
                        } else {
                            p.mediaPlayer = new MediaPlayer();
                            try {
                                p.mediaPlayer.setDataSource(p.fileName);
                                p.mediaPlayer.prepare();
                                p.mediaPlayer.start();
                            } catch (IOException e) {
                                Log.e(LOG_TAG, "prepare() failed");
                            }
                            p.color = getResources().getColor(R.color.colorAccent);
                            p.isPlaying = true;
                        }
                        rv.setAdapter(adapter);
                    }

                    @Override
                    public void onLongItemClick(View view, final int position) {
                        final Pad p = pads.get(position);
                        if (p.isPlaying) {
                            if (p.mediaPlayer != null) {
                                p.mediaPlayer.release();
                                p.mediaPlayer = null;
                            }
                            p.color = getResources().getColor(R.color.colorPrimary);
                            p.isPlaying = false;
                            rv.setAdapter(adapter);
                        }
                        //TODO MINOR design dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle(p.name)
                                .setItems(getResources().getStringArray(R.array.actions_array), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            pads.get(position).mediaPlayer = new MediaPlayer();
                                            try {
                                                p.mediaPlayer.setDataSource(p.fileName);
                                                p.mediaPlayer.prepare();
                                                p.mediaPlayer.start();
                                            } catch (IOException e) {
                                                Log.e(LOG_TAG, "prepare() failed");
                                            }
                                            p.color = getResources().getColor(R.color.colorAccent);
                                            p.isPlaying = true;
                                        }
                                        if (which == 1){
                                            AlertDialog.Builder editBuilder = new AlertDialog.Builder(MainActivity.this);
                                            // Get the layout inflater
                                            LayoutInflater inflater = MainActivity.this.getLayoutInflater();

                                            // Inflate and set the layout for the dialog
                                            // Pass null as the parent view because its going in the dialog layout
                                            View editView = inflater.inflate(R.layout.edit_dialog, null);
                                            final EditText newName = (EditText) editView.findViewById(R.id.new_name);

                                            newName.setText(p.name);

                                            editBuilder.setView(editView)
                                                    .setTitle("Edit " + p.name)
                                                    // Add action buttons
                                                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            String oldName = p.name;
                                                            String oldFileName = p.fileName;
                                                            p.name = String.valueOf(newName.getText());
                                                            p.fileName = oldFileName.substring(0, oldFileName.length() - (oldName.length() + 4)) + newName.getText()  + ".3gp";
                                                            File newFile = new File(p.fileName);
                                                            p.file.renameTo(newFile);
                                                            rv.setAdapter(adapter);
                                                        }
                                                    })
                                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                        }
                                                    }).show();
                                        }
                                        if (which == 2) {
                                            AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(MainActivity.this);
                                            deleteBuilder.setTitle("Delete " + p.name + " ?");
                                            deleteBuilder.setMessage("Do you really want to delete this sound ?");
                                            deleteBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    p.file.delete();
                                                    pads.remove(position);
                                                    rv.setAdapter(adapter);
                                                }
                                            });
                                            deleteBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // User cancelled the dialog
                                                }
                                            }).show();
                                        }
                                        rv.setAdapter(adapter);
                                    }
                                }).show();
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

