package edu.sim.whiff.UI.HomePage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.net.VpnService;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import org.jnetpcap.PcapService;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.sim.whiff.FileManager;
import edu.sim.whiff.R;
import edu.sim.whiff.UI.PacketFileRecyclerViewAdapter;
import edu.sim.whiff.Utils;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;


public class HomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomePageViewInterface ,
        PacketFileRecyclerViewAdapter.ItemClickListener {

    public FloatingActionButton fabStart;
    public FloatingActionButton fabStop;
    public HomePagePresenterInterface presenter;
    private static final int VPN_REQUEST_CODE = 0x0F;
    private static final String TAG = HomePage.class.getSimpleName();

    RecyclerView mRecyclerView;
    PacketFileRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        connectWithPresenter();

        fabStart    = (FloatingActionButton) findViewById(R.id.fab_start);
        fabStop     = (FloatingActionButton) findViewById(R.id.fab_stop);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        PcapService.getIsRunning().subscribe(isRunning -> {
            if (isRunning) {
                Log.e(TAG,"Packet Capture Service - Started");
            } else {
                Log.e(TAG,"Packet Capture Service - Stopped");
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
        presenter = new HomePagePresenter(this);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_Packet_Capture) {
            //TODO Create new activity
        } else if (id == R.id.nav_wep_crack) {
            Intent WEPActivity = new Intent (this, WEPCrack.class);
            startActivity(WEPActivity);
        } else if (id == R.id.nav_Import_File) {

        } else if (id == R.id.nav_help_faq) {


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void refreshList() {

        if (mAdapter != null) {
            mAdapter.setClickListener(null);
            mAdapter = null;
        }
        List<File> files = presenter.listPacketFiles();
        //sortFilesInDescOrder(files);
        mAdapter = new PacketFileRecyclerViewAdapter(this, files);
        mAdapter.setClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
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
        Toast.makeText(this, f.getAbsolutePath(), Toast.LENGTH_LONG).show();
    }
}
