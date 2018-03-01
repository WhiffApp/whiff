package com.app.whiff.whiff.UI.PacketDbContent;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import com.app.whiff.whiff.NonRootScanner.CaptureDAO;
import com.app.whiff.whiff.NonRootScanner.CaptureItem;
import com.app.whiff.whiff.NonRootScanner.PacketContentFilter;
import com.app.whiff.whiff.NonRootScanner.PacketContentFilterQuery;
import com.app.whiff.whiff.R;

/**
 * Provides CRUD data access functions to the tables stored
 * in the database.
 *
 * @author Yeo Pei Xuan
 */

public class PacketDbContentPage extends AppCompatActivity implements
        PacketDbContentPageViewInterface,
        PacketDbContentRecyclerViewAdapter.ItemClickListener {

    private static final String TAG = PacketDbContentPage.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private PacketDbContentRecyclerViewAdapter mAdapter;
    private PacketDbContentPagePresenterInterface mPresenter;
    private LinearLayout mProgressBar;
    private long mCaptureID;
    private PacketContentFilter mFilter;

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
        String captureDesc = intent.getStringExtra("CaptureDesc");
        setTitle(captureDesc);

        mCaptureID = intent.getLongExtra("CaptureID", 0);
        PacketContentFilter contentFilter = intent.getParcelableExtra("PACKET_CONTENT_FILTER");

        PacketContentFilterQuery aQuery = new PacketContentFilterQuery();
        aQuery.captureID = mCaptureID;
        aQuery.contentFilter = contentFilter;

        LoadCaptureItemsTask aTask = new LoadCaptureItemsTask();
        aTask.execute(aQuery);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.filter_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void connectWithPresenter()
    {
        mPresenter = new PacketDbContentPresenter(this,
                new CaptureDAO(this.getApplicationContext()));
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

    private class LoadCaptureItemsTask extends AsyncTask<PacketContentFilterQuery, Void, List<CaptureItem>> {

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
        protected List<CaptureItem> doInBackground(PacketContentFilterQuery... queries) {

            List<CaptureItem> items = new ArrayList<CaptureItem>(0);

            try {

                if (queries != null && queries.length > 0) {
                    PacketContentFilterQuery query = queries[0];
                    items = mPresenter.getCaptureItems(query);
                }
            } catch(Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return items;
        }
    }
}
