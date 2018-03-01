package com.app.whiff.whiff.UI.ImportPacketFile;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import java.io.File;
import java.util.List;

import com.app.whiff.whiff.NonRootScanner.CaptureDAO;
import com.app.whiff.whiff.NonRootScanner.FileManager;
import com.app.whiff.whiff.R;


public class ImportPacketFilePage extends AppCompatActivity implements
        ImportPacketFilePageViewInterface, ImportPacketFileRecyclerViewAdapter.ItemClickListener {

    private static final String TAG = ImportPacketFilePage.class.getSimpleName();
    private ImportPacketFilePagePresenterInterface mPresenter;
    private LinearLayout mProgressBar;

    RecyclerView mRecyclerView;
    ImportPacketFileRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_packetfile_page);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressBar = (LinearLayout) findViewById(R.id.progressLayout);

        // set up the RecyclerView
        mRecyclerView = findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        connectWithPresenter();

        FileManager.init();

        refreshList();
    }

    public void connectWithPresenter()
    {
        mPresenter = new ImportPacketFilePagePresenter(this,
                new CaptureDAO(this));
    }

    @Override
    public void onBackPressed() {

        //stopImport();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

    private void refreshList() {

        if (mAdapter != null) {
            mAdapter.setClickListener(null);
            mAdapter = null;
        }

        List<File> files = mPresenter.listPacketFiles();
        if (files.size() > 0) {
            mAdapter = new ImportPacketFileRecyclerViewAdapter(this, files);
            mAdapter.setClickListener(this);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void ShowProgressBar(boolean visibility) {

        setProgressBarIndeterminateVisibility(visibility);
        if (visibility) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        File f = mAdapter.getItem(position);

        String format = getText(R.string.importFile).toString();
        String dialogMsg = String.format(format, f.getName());
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_activity_import_packetfile_page)
                .setMessage(dialogMsg)
                .setCancelable(true)
                .setPositiveButton(R.string.dialogYesButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        ImportFileAsyncTask aTask = new ImportFileAsyncTask();
                        aTask.execute(f);

                    }
                })
                .setNegativeButton(R.string.dialogNoButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.cancel();
                    }
                })
                .show();
    }

    private void notifyImportResult(File file, long rows) {

        if (rows == 0) {

            String msg = "Failed to import! It might already imported before.";
            new AlertDialog.Builder(this)
                    .setTitle(R.string.title_activity_import_packetfile_page)
                    .setMessage(msg)
                    .setCancelable(true)
                    .setPositiveButton(R.string.dialogOkButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.cancel();
                        }
                    })
                    .show();

        } else {

            String format = "%d rows imported from file '%s'.";
            String msg = String.format(format, rows, file.getName());
            new AlertDialog.Builder(this)
                    .setTitle(R.string.title_activity_import_packetfile_page)
                    .setMessage(msg)
                    .setCancelable(true)
                    .setPositiveButton(R.string.dialogOkButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .show();
        }
    }

    private class ImportFileAsyncTask extends AsyncTask<File, Void, Long> {

        private File importedFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ShowProgressBar(true);
        }

        @Override
        protected void onPostExecute(Long rows) {
            super.onPostExecute(rows);

            try {

                notifyImportResult(importedFile, rows);

            } catch(Exception e) {

            }
            ShowProgressBar(false);
        }

        @Override
        protected Long doInBackground(File... files) {

            long rowsCount = 0;

            try {

                if (files != null && files.length > 0) {
                    importedFile = files[0];
                    rowsCount = mPresenter.importPacketFile(importedFile);

                }
            } catch(Exception e) {

            }
            return rowsCount;
        }
    }
}
