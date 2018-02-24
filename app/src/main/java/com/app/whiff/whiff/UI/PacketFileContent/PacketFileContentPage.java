package com.app.whiff.whiff.UI.PacketFileContent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.io.File;
import java.util.List;

import com.app.whiff.whiff.NonRootScanner.CaptureItem;
import com.app.whiff.whiff.R;


public class PacketFileContentPage extends AppCompatActivity implements
        PacketFileContentPageViewInterface,
        PacketFileContentRecyclerViewAdapter.ItemClickListener {

    private static final String TAG = PacketFileContentPage.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private PacketFileContentRecyclerViewAdapter mAdapter;
    private PacketFileContentPagePresenterInterface presenter;
    private String mCaptureFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packet_file_content_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        connectWithPresenter();

        Intent intent = getIntent();
        mCaptureFilePath = intent.getStringExtra("CaptureFile");

        File f = new File(mCaptureFilePath);
        setTitle(f.getName());

        // set up the RecyclerView
        mRecyclerView = findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshList();
    }

    public void connectWithPresenter()
    {
        presenter = new PacketFileContentPresenter(this);
    }

    private void refreshList() {

        if (mAdapter != null) {
            mAdapter.setClickListener(null);
            mAdapter = null;
        }

        List<CaptureItem> items = presenter.getCaptureItems(mCaptureFilePath);
        mAdapter = new PacketFileContentRecyclerViewAdapter(this, items);
        mAdapter.setClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View view, int position) {
        CaptureItem item = mAdapter.getItem(position);

        new AlertDialog.Builder(this)
                .setTitle(item.timestamp.toString())
                .setMessage(item.text).show();
    }
}
