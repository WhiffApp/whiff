package edu.sim.whiff.UI.PacketDb;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Collections;
import java.util.List;

import edu.sim.whiff.Capture;
import edu.sim.whiff.Common.JDateTimeTransform;
import edu.sim.whiff.Common.SizeFormatter;
import edu.sim.whiff.R;


public class PacketDbRecyclerViewAdapter extends RecyclerView.Adapter<PacketDbRecyclerViewAdapter.ViewHolder> {

    private List<Capture> mData = Collections.emptyList();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public PacketDbRecyclerViewAdapter(Context context, List<Capture> data) {
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
        Capture capture = mData.get(position);
        setData(holder, capture);
    }

    private void setData(ViewHolder holder, Capture c) {

        String name = c.desc;
        holder.nameTextView.setText(name);

        //String size = SizeFormatter.convertToStringRepresentation(c.fileSize);
        //holder.sizeTextView.setText(size);

        //String path = c.fileName.replace(Environment.getExternalStorageDirectory().getAbsolutePath(), "");
        //holder.pathTextView.setText(path);
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
    public Capture getItem(int id) {
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