package com.app.whiff.whiff.UI.PacketFile;

import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
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

import org.jnetpcap.PcapService;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.app.whiff.whiff.NonRootScanner.FileManager;
import com.app.whiff.whiff.NonRootScanner.UI.NonRootScanner;
import com.app.whiff.whiff.R;
import com.app.whiff.whiff.RootScanner.UI.RootScanner;
import com.app.whiff.whiff.UI.PacketFileContent.PacketFileContentPage;


public class PacketFilePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PacketFilePageViewInterface,
        PacketFileRecyclerViewAdapter.ItemClickListener {

    public FloatingActionButton fabStart;
    public FloatingActionButton fabStop;
    public PacketFilePagePresenterInterface presenter;
    private static final int VPN_REQUEST_CODE = 0x0F;
    private static final String TAG = PacketFilePage.class.getSimpleName();

    RecyclerView mRecyclerView;
    PacketFileRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packetfile_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        connectWithPresenter();

        fabStart    = findViewById(R.id.fab_start);
        fabStop     = findViewById(R.id.fab_stop);
        fabStop.hide();
        fabStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.StartClicked();
                //TODO call packet listener here
                //Snackbar.make(view, "Start clicked", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                startOrStopCapture();
            }
        });
        fabStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.StopClicked();
                //TODO stop listening here
                //Snackbar.make(view, "Stop clicked", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                startOrStopCapture();
                refreshList();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        PcapService.getIsRunning().subscribe(isRunning -> {
            if (isRunning) {
                Log.e(TAG,"Packet Capture Service - Started");
                //presenter.StartClicked();
            } else {
                Log.e(TAG,"Packet Capture Service - Stopped");
                //presenter.StopClicked();
            }
        });

        FileManager.init();

        // set up the RecyclerView
        mRecyclerView = findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshList();
    }

    private void startOrStopCapture()
    {
        Intent vpnIntent = VpnService.prepare(this);
        if (vpnIntent != null)
            startActivityForResult(vpnIntent, VPN_REQUEST_CODE);
        else
            onActivityResult(VPN_REQUEST_CODE, RESULT_OK, null);
    }

    private void stopCapture() {

        boolean isRunning = PcapService.isRunning();
        if (isRunning) {
            Intent i = getServiceIntent(PcapService.ACTION_STOP);
            startService(i);
        }
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data) {

        if (result == RESULT_OK) {

            Boolean isRunning = PcapService.isRunning();
            Intent i = isRunning
                    ? getServiceIntent(PcapService.ACTION_STOP)
                    : getServiceIntent(PcapService.ACTION_START);

            if (isRunning == Boolean.FALSE) {
                //  We can show UI here to allow user to specify filter criteria for the
                //  packet capturing
                i.putExtra(PcapService.PCAP_LOG_FILENAME,   FileManager.createNewPacketFile().getPath());
                /*
                //  TODO
                i.putExtra(PacketCaptureService.CAPTURE_NAME,   Utils.getUniqueTimestampName());
                i.putExtra(PacketCaptureService.PCF_PROTO_TYPE, "TCP/UDP/ICMP/HTTP/HTTPS");
                i.putExtra(PacketCaptureService.PCF_SRC_IP,     "");
                i.putExtra(PacketCaptureService.PCF_SRC_PORT,   "");
                i.putExtra(PacketCaptureService.PCF_DST_IP,     "");
                i.putExtra(PacketCaptureService.PCF_DST_PORT,   "");
                */
            }

            startService(i);
        }
    }

    private Intent getServiceIntent(String action) {
        Intent i = new Intent( this, PcapService.class);
        i.setAction(action);

        return i;
    }

    public void hideFabStart()
    {
        fabStart.hide();
        fabStop.show();
    }

    public void hideFabStop()
    {
        fabStop.hide();
        fabStart.show();
    }

    public void connectWithPresenter()
    {
        presenter = new PacketFilePagePresenter(this);
    }

    @Override
    public void onBackPressed() {

        stopCapture();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
        List<File> files = presenter.listPacketFiles();
        //sortFilesInDescOrder(files);
        if (files.size() > 0) {
            mAdapter = new PacketFileRecyclerViewAdapter(this, files);
            mAdapter.setClickListener(this);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
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

        if (id == R.id.nav_homepage) {
            Intent HomePageActivity = new Intent(this,
                    com.app.whiff.whiff.UI.HomePage.HomePage.class);
            startActivity(HomePageActivity);

        } else if (id == R.id.nav_Packet_Capture_File) {
            Intent NonRootScannerActivity = new Intent(this,
                    com.app.whiff.whiff.UI.PacketFile.PacketFilePage.class);
            startActivity(NonRootScannerActivity);

        } else if (id == R.id.nav_Packet_Capture_Db) {
            Intent NonRootTransportActivity = new Intent(this,
                    com.app.whiff.whiff.UI.PacketDb.PacketDbPage.class);
            startActivity(NonRootTransportActivity);

        } else if (id == R.id.nav_Import_File) {
            Intent ViewActivity = new Intent (this,
                    com.app.whiff.whiff.UI.View.ViewPage.class);
            startActivity(ViewActivity);

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
