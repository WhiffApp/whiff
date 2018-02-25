package com.app.whiff.whiff.UI.PacketFileContent;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.app.whiff.whiff.NonRootScanner.CaptureItem;
import com.app.whiff.whiff.R;


public class PacketFileContentPage extends AppCompatActivity implements
        PacketFileContentPageViewInterface,
        PacketFileContentRecyclerViewAdapter.ItemClickListener {

    private static final String TAG = PacketFileContentPage.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private PacketFileContentRecyclerViewAdapter mAdapter;
    private PacketFileContentPagePresenterInterface mPresenter;
    private LinearLayout mProgressBar;
    private String mCaptureFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packet_file_content_page);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressBar = (LinearLayout) findViewById(R.id.progressLayout);

        // set up the RecyclerView
        mRecyclerView = findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        connectWithPresenter();

        Intent intent = getIntent();
        mCaptureFilePath = intent.getStringExtra("CaptureFile");

        File f = new File(mCaptureFilePath);
        setTitle(f.getName());

        LoadCaptureItemsTask aTask = new LoadCaptureItemsTask();
        aTask.execute(mCaptureFilePath);
    }

    public void connectWithPresenter()
    {
        mPresenter = new PacketFileContentPresenter(this);
    }

    private void ShowProgressBar(boolean visibility) {

        setProgressBarIndeterminateVisibility(visibility);
        if (visibility) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void refreshList(List<CaptureItem> items) {

        if (mAdapter != null) {
            mAdapter.setClickListener(null);
            mAdapter = null;
        }

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

    private class LoadCaptureItemsTask extends AsyncTask<String, Void, List<CaptureItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ShowProgressBar(true);
        }

        @Override
        protected void onPostExecute(List<CaptureItem> items) {
            super.onPostExecute(items);

            try {

                if(items != null){
                    refreshList(items);
                }

            } catch(Exception e) {

            }
            ShowProgressBar(false);
        }

        @Override
        protected List<CaptureItem> doInBackground(String... strings) {

            List<CaptureItem> items = new ArrayList<CaptureItem>(0);

            try {

                if (strings != null && strings.length > 0) {
                    String filename = strings[0];
                    items = mPresenter.getCaptureItems(filename);
                }
            } catch(Exception e) {

            }
            return items;
        }
    }
}
