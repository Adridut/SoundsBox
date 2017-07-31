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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements PadAdapter.ItemClickListener {

    public static final int ADD_REQUEST_CODE = 1;
    public static final int INFOS_REQUEST_CODE = 2;
    ArrayList<Pad> pads;
    RecyclerView rv;
    PadAdapter adapter;
    private MediaPlayer mPlayer = null;
    private static final String LOG_TAG = "AudioRecordTest";
    ImageButton add;
    String errorMessage;
    Utils utils = new Utils();

    //TODO design
    //TODO MINOR translate strings
    //TODO MINOR change the logo


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // retrieving files
        final File soundsDir;
        soundsDir = new File(getFilesDir().getAbsolutePath());
        File[] soundsFiles = soundsDir.listFiles();

        pads = new ArrayList<>();

        for (File f : soundsFiles) {
            String name = f.getName();
            if (name.endsWith(".mp3")) {
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
                                p.mediaPlayer.setLooping(true);
                                Log.i("FILENAME", p.fileName);
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
                        //TODO use the toolbar instead of a dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setItems(getResources().getStringArray(R.array.actions_array), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
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
                                        if (which == 1) {
                                            AlertDialog.Builder editBuilder = new AlertDialog.Builder(MainActivity.this);
                                            // Get the layout inflater
                                            LayoutInflater inflater = MainActivity.this.getLayoutInflater();

                                            // Inflate and set the layout for the dialog
                                            // Pass null as the parent view because its going in the dialog layout
                                            View editView = inflater.inflate(R.layout.edit_dialog, null);
                                            final EditText newNameET = (EditText) editView.findViewById(R.id.new_name);

                                            newNameET.setText(p.name);

                                            editBuilder.setView(editView)
                                                    .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            String newName = String.valueOf(newNameET.getText());
                                                            if (utils.checkName(newName, soundsDir) == 1){
                                                                String oldName = p.name;
                                                                String oldFileName = p.fileName;
                                                                p.name = newName;
                                                                p.fileName = oldFileName.substring(0, oldFileName.length() - (oldName.length() + 4)) + newNameET.getText() + ".mp3";
                                                                File newFile = new File(p.fileName);
                                                                p.file.renameTo(newFile);
                                                                rv.setAdapter(adapter);
                                                            } else if (!newName.equals(p.name)){
                                                                if (utils.checkName(newName, soundsDir) == 0){
                                                                    errorMessage = getString(R.string.no_name_error);
                                                                } else {
                                                                    errorMessage = getString(R.string.used_name_error);
                                                                }
                                                                AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(MainActivity.this);
                                                                deleteBuilder.setTitle(R.string.error);
                                                                deleteBuilder.setMessage(errorMessage);
                                                                deleteBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                    }
                                                                }).show();
                                                            }
                                                        }
                                                    })
                                                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                        }
                                                    }).show();
                                        }
                                        if (which == 2) {
                                            AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(MainActivity.this);
                                            deleteBuilder.setMessage(R.string.delete_message);
                                            deleteBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    p.file.delete();
                                                    pads.remove(position);
                                                    rv.setAdapter(adapter);
                                                }
                                            });
                                            deleteBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
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


        add = (ImageButton) findViewById(R.id.add_button);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent i = new Intent(getApplicationContext(), Infos.class);
            startActivityForResult(i, INFOS_REQUEST_CODE);
        }

        return super.onOptionsItemSelected(item);
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
            }
        }
    }

    @Override
    public void onItemClick(View view, int position) {

    }

}

