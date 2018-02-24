package com.app.whiff.whiff.UI.PacketDbContent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

import com.app.whiff.whiff.NonRootScanner.CaptureDAO;
import com.app.whiff.whiff.NonRootScanner.CaptureItem;
import com.app.whiff.whiff.R;


public class PacketDbContentPage extends AppCompatActivity implements
        PacketDbContentPageViewInterface,
        PacketDbContentRecyclerViewAdapter.ItemClickListener {

    private static final String TAG = PacketDbContentPage.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private PacketDbContentRecyclerViewAdapter mAdapter;
    private PacketDbContentPagePresenterInterface presenter;
    private long mCaptureID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packet_file_content_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        connectWithPresenter();

        Intent intent = getIntent();
        mCaptureID = intent.getLongExtra("CaptureID", 0);
        String captureDesc = intent.getStringExtra("CaptureDesc");

        setTitle(captureDesc);

        // set up the RecyclerView
        mRecyclerView = findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshList();
    }

    public void connectWithPresenter()
    {
        presenter = new PacketDbContentPresenter(this,
                new CaptureDAO(this.getApplicationContext()));
    }

    private void refreshList() {

        if (mAdapter != null) {
            mAdapter.setClickListener(null);
            mAdapter = null;
        }

        List<CaptureItem> items = presenter.getCaptureItems(mCaptureID);
        if (items.size() > 0) {
            mAdapter = new PacketDbContentRecyclerViewAdapter(this, items);
            mAdapter.setClickListener(this);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        CaptureItem item = mAdapter.getItem(position);

        new AlertDialog.Builder(this)
                .setTitle(item.timestamp.toString())
                .setMessage(item.text).show();
    }
}
