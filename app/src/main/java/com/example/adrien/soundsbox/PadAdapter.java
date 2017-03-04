package com.example.adrien.soundsbox;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by adrien on 04/03/17.
 */

public class PadAdapter extends RecyclerView.Adapter<PadAdapter.ViewHolder> {

    private ArrayList<Pad> mPads = new ArrayList<>();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public PadAdapter(Context context, ArrayList<Pad> pads){
        this.mInflater = LayoutInflater.from(context);
        this.mPads = pads;
    }

    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_cell, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String animal = mPads.get(position).name;
        holder.pad.setText(animal);
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mPads.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public Button pad;

        public ViewHolder(View itemView) {
            super(itemView);
            pad = (Button) itemView.findViewById(R.id.pad);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public String getName(int id) {
        return mPads.get(id).name;
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
