package com.app.whiff.whiff.UI.PacketDbContent;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import com.app.whiff.whiff.NonRootScanner.CaptureItem;
import com.app.whiff.whiff.NonRootScanner.Common.SizeFormatter;
import com.app.whiff.whiff.R;

public class PacketDbContentRecyclerViewAdapter extends RecyclerView.Adapter<PacketDbContentRecyclerViewAdapter.ViewHolder> {

    private List<CaptureItem> mData = Collections.emptyList();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public PacketDbContentRecyclerViewAdapter(Context context, List<CaptureItem> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_packet_file_content, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the textview in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CaptureItem item = mData.get(position);
        holder.txt_address_from.setText(item.sourceAddress + ":" + item.sourcePort);
        holder.txt_address_to.setText(item.destinationAddress + ":" + item.destinationPort);
        holder.txt_type.setText(item.protocol);
        holder.txt_size.setText(SizeFormatter.convertToStringRepresentation(item.length));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txt_address_from;
        public TextView txt_address_to;
        public TextView txt_type;
        public TextView txt_size;

        public ViewHolder(View itemView) {
            super(itemView);
            txt_address_from = itemView.findViewById(R.id.txt_address_from);
            txt_address_to   = itemView.findViewById(R.id.txt_address_to);
            txt_type = itemView.findViewById(R.id.txt_type);
            txt_size = itemView.findViewById(R.id.txt_size);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public CaptureItem getItem(int id) {
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