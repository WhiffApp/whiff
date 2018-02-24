package edu.sim.whiff.UI.PacketFile;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.Collections;
import java.util.List;

import edu.sim.whiff.R;
import edu.sim.whiff.Common.JDateTimeTransform;
import edu.sim.whiff.Common.SizeFormatter;

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
        setData(holder, pcapFile);
    }

    private void setData(ViewHolder holder, File f) {

        String time = f.getName().substring(0, f.getName().length() - 5);
        String name = new JDateTimeTransform().parse("yyyyMMdd_hhmmss", time).toString("yyyy-MM-dd hh:mm:ss");
        holder.nameTextView.setText(name);

        String size = SizeFormatter.convertToStringRepresentation(f.length());
        holder.sizeTextView.setText(size);

        String path = f.getAbsolutePath().replace(Environment.getExternalStorageDirectory().getAbsolutePath(), "");
        holder.pathTextView.setText(path);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView nameTextView;
        public TextView sizeTextView;
        public TextView pathTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.txt_name);
            sizeTextView = itemView.findViewById(R.id.txt_size);
            pathTextView = itemView.findViewById(R.id.txt_path);

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