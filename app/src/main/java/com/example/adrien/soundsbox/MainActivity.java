package com.example.adrien.soundsbox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements PadAdapter.ItemClickListener {

    public static final int ADD_REQUEST_CODE = 1;
    ArrayList<Pad> pads;
    RecyclerView rv;
    PadAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pads = new ArrayList<>();

        rv = (RecyclerView) findViewById(R.id.pads_list);
        rv.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new PadAdapter(this, pads);
        adapter.setClickListener(this);
        rv.setAdapter(adapter);


        Button add = (Button) findViewById(R.id.add_button);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                pads.add(new Pad(name));
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
