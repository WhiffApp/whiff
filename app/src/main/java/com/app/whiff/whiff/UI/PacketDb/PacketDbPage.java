package com.app.whiff.whiff.UI.PacketDb;


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

import java.util.List;

import com.app.whiff.whiff.NonRootScanner.Capture;
import com.app.whiff.whiff.NonRootScanner.CaptureDAO;
import com.app.whiff.whiff.NonRootScanner.FileManager;
import com.app.whiff.whiff.NonRootScanner.PacketCaptureService;
import com.app.whiff.whiff.R;
import com.app.whiff.whiff.UI.PacketDbContent.PacketDbContentPage;


public class PacketDbPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PacketDbPageViewInterface,
        PacketDbRecyclerViewAdapter.ItemClickListener {

    public FloatingActionButton fabStart;
    public FloatingActionButton fabStop;
    public PacketDbPagePresenterInterface presenter;
    private static final int VPN_REQUEST_CODE = 0x0F;
    private static final String TAG = PacketDbPage.class.getSimpleName();

    RecyclerView mRecyclerView;
    PacketDbRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packetdb_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        connectWithPresenter();

        fabStart    = (FloatingActionButton) findViewById(R.id.fab_start);
        fabStop     = (FloatingActionButton) findViewById(R.id.fab_stop);
        fabStop.hide();

        fabStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.StartClicked();

                startOrStopCapture();
            }
        });
        fabStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.StopClicked();

                startOrStopCapture();
                refreshList();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        PacketCaptureService.getIsRunning().subscribe(isRunning -> {
            if (isRunning) {
                Log.e(TAG,"Packet Capture Service - Started");
                presenter.StartClicked();

            } else {
                Log.e(TAG,"Packet Capture Service - Stopped");
                presenter.StopClicked();
            }
        });

        FileManager.init();

        // set up the RecyclerView
        mRecyclerView = findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshList();
    }

    public void connectWithPresenter()
    {
        presenter = new PacketDbPagePresenter(this,
                new CaptureDAO(this.getApplicationContext()));
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

        boolean isRunning = PacketCaptureService.isRunning();
        if (isRunning) {
            Intent i = getServiceIntent(PacketCaptureService.ACTION_STOP);
            startService(i);
        }
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data) {

        if (result == RESULT_OK) {

            Boolean isRunning = PacketCaptureService.isRunning();
            Intent i = isRunning
                    ? getServiceIntent(PacketCaptureService.ACTION_STOP)
                    : getServiceIntent(PacketCaptureService.ACTION_START);

            startService(i);
        }
    }

    private Intent getServiceIntent(String action) {
        Intent i = new Intent( this, PacketCaptureService.class);
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


    @Override
    public void onBackPressed() {

        stopCapture();

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
        getMenuInflater().inflate(R.menu.home_page, menu);
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

        if (id == R.id.export_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshList() {

        if (mAdapter != null) {
            mAdapter.setClickListener(null);
            mAdapter = null;
        }

        List<Capture> files = presenter.getCaptureList();
        mAdapter = new PacketDbRecyclerViewAdapter(this, files);
        mAdapter.setClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View view, int position) {
        Capture c = mAdapter.getItem(position);
        //Toast.makeText(this, c.fileName, Toast.LENGTH_LONG).show();
        Intent i = new Intent(this, PacketDbContentPage.class);
        i.putExtra("CaptureID", c.ID);
        i.putExtra("CaptureName", c.name);
        i.putExtra("CaptureDesc", c.desc);
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
