/*
	Author: 		Yeo Pei Xuan
	Date Created:	19/02/2018
	Description: 	An Android UI for Listview. Purpose is to recycle hidden rows which scrolled passed
					the screen to display new items, to optimize memory for display.
*/
package edu.sim.whiff.UI;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.Collections;
import java.util.List;

import edu.sim.whiff.R;

public class PacketFileRecyclerViewAdapter extends RecyclerView.Adapter<PacketFileRecyclerViewAdapter.ViewHolder> {

    private List<File> mData = Collections.emptyList();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public PacketFileRecyclerViewAdapter(Context context, List<File> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_packet_file, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the textview in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        File pcapFile = mData.get(position);
        holder.myTextView.setText(pcapFile.getName());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView myTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.txtPacketFile);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public File getItem(int id) {
        return mData.get(id);
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