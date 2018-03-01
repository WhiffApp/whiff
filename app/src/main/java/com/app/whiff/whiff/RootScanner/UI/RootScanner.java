package com.app.whiff.whiff.RootScanner.UI;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.app.whiff.whiff.DBHandler;
import com.app.whiff.whiff.NonRootScanner.FileManager;
import com.app.whiff.whiff.R;
import com.app.whiff.whiff.NonRootScanner.UI.NonRootScanner;
import com.app.whiff.whiff.RootScanner.TCPdump;
import com.app.whiff.whiff.RootScanner.TCPdumpService;
import com.app.whiff.whiff.UI.PacketFile.PacketFileRecyclerViewAdapter;
import com.app.whiff.whiff.UI.PacketFileContent.PacketFileContentPage;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class RootScanner extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RootScannerViewInterface,
        PacketFileRecyclerViewAdapter.ItemClickListener {

    // UI elements
    public TextView TV1;
    public FloatingActionButton fabStart;
    public FloatingActionButton fabStop;

    // Presenter
    public RootScannerPresenterInterface presenter;

    // Display elements
    RecyclerView mRecyclerView;
    PacketFileRecyclerViewAdapter mAdapter;

    // TCPdump helper
    public TCPdump tcpdump;

    // Database
    public DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_scanner);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        connectWithPresenter(); // RootScannerPresenter object
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        tcpdump = new TCPdump(getApplicationContext());
        tcpdump.installTCPdump();
        // File Manager
        FileManager.init();

        TV1 = (TextView) findViewById(R.id.TV1);
        fabStart = (FloatingActionButton) findViewById(R.id.fab_start);
        fabStop = (FloatingActionButton) findViewById(R.id.fab_stop);
        if (!isServiceRunning(TCPdumpService.class)) {
            hideFabStop();
        } else {
            hideFabStart();
        }

        fabStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.StartClicked();
                String dir = Environment.getExternalStorageDirectory().toString() + "/Whiff/" + FileManager.generateNewFileName();
                EditText edit = (EditText)findViewById(R.id.rootParameters);
                String parameters = (String) edit.getText().toString();
                String TCPdumpParams = "-U -w " + dir + " " + parameters;
                edit.setText("",TextView.BufferType.EDITABLE);

                if (!isServiceRunning(TCPdumpService.class)) {
                    startTCPdumpService(TCPdumpParams);
                }
                Snackbar.make(view, "Start clicked", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Log.d("RootScanner MSG","Start Clicked");
            }
        });
        fabStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.StopClicked();
                String TCPdumpParams;
                TCPdumpParams = "stop";
                if (isServiceRunning(TCPdumpService.class)) {
                    stopTCPdumpService();
                    refreshList();
                }
                Snackbar.make(view, "Stop clicked", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Log.d("RootScanner MSG","Stop Clicked");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // Database
        db = new DBHandler(this, null,null,1);

        presenter.ActivityStarted();


        // set up the RecyclerView
        /*mRecyclerView = findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshList();*/
    }

    public void installTCPdump() {
        if (tcpdump != null)
            tcpdump.installTCPdump();
        else {
            tcpdump = new TCPdump(getApplicationContext());
            tcpdump.installTCPdump();
        }
    }

    public void startTCPdumpService(String params) {
        Intent intent = new Intent(RootScanner.this, TCPdumpService.class);
        intent.putExtra(TCPdumpService.ACTION_START,params);
        startService(intent);
    }

    public void stopTCPdumpService() {
        new TCPdump(getApplicationContext()).stopSniff();
        Intent intent = new Intent(RootScanner.this, TCPdumpService.class);
        stopService(intent);
    }

    public boolean isServiceRunning(Class s) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (s.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void hideFabStart() {
        fabStart.hide();
        fabStop.show();
    }

    public void hideFabStop() {
        fabStop.hide();
        fabStart.show();
    }

    public void showMessage(String message) {
        TV1.setText(message);
    }

    public void connectWithPresenter() {
        presenter = new RootScannerPresenter(this);
    }

    @Override
    public void onBackPressed() {
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

    public class ResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP =
                "com.app.whiff.whiff.intent.action.MESSAGE_PROCESSED";

        @Override
        public void onReceive(Context context, Intent intent) {
            // String text = intent.getStringExtra(TCPdumpService.PARAM_OUT_MESSAGE);
            // System.out.println(text);
        }
    }

    // Display elements
    private void refreshList() {

        if (mAdapter != null) {
            mAdapter.setClickListener(null);
            mAdapter = null;
        }
        List<File> files = presenter.listPacketFiles();
        //sortFilesInDescOrder(files);
        /*if (files.size() > 0) {
            mAdapter = new PacketFileRecyclerViewAdapter(this, files);
            mAdapter.setClickListener(this);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }*/
    }

    private void sortFilesInDescOrder(List<File> files) {
        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                return f1.lastModified() > f2.lastModified() ? 1 : 0;
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        File f = mAdapter.getItem(position);
        //Toast.makeText(this, f.getAbsolutePath(), Toast.LENGTH_LONG).show();
        Intent i = new Intent(this, PacketFileContentPage.class);
        i.putExtra("CaptureFile", f.getAbsolutePath());
        startActivity(i);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_root_packet_capture) {
            Intent RootScannerActivity = new Intent(this, com.app.whiff.whiff.RootScanner.UI.RootScanner.class);
            startActivity(RootScannerActivity);

        } else if (id == R.id.nav_non_root_packet_capture) {
            Intent NonRootScannerActivity = new Intent(this, com.app.whiff.whiff.UI.PacketFile.PacketFilePage.class);
            startActivity(NonRootScannerActivity);

        } else if (id == R.id.nav_non_root_sniffer_transport) {
            Intent NonRootTransportActivity = new Intent(this, com.app.whiff.whiff.UI.PacketDb.PacketDbPage.class);
            startActivity(NonRootTransportActivity);

        } else if (id == R.id.nav_Import_File) {
            Intent ImportActivity = new Intent (this, com.app.whiff.whiff.UI.ImportPacketFile.ImportPacketFilePage.class);
            startActivity(ImportActivity);

        } else if (id == R.id.nav_help_faq) {
            // Intent helpActivity = new Intent (this, com.app.whiff.whiff.help.class);
            // startActivity(helpActivity);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
