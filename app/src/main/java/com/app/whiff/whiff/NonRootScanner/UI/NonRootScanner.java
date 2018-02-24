package com.app.whiff.whiff.NonRootScanner.UI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.net.VpnService;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
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

import com.app.whiff.whiff.UI.HomePage.WEPCrack;
import com.app.whiff.whiff.NonRootScanner.PacketCaptureService;
import com.app.whiff.whiff.R;
import com.app.whiff.whiff.NonRootScanner.Utils;

public class NonRootScanner extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NonRootScannerViewInterface {
    public FloatingActionButton fabStart;
    public FloatingActionButton fabStop;
    public NonRootScannerPresenterInterface presenter;
    private static final int VPN_REQUEST_CODE = 0x0F;
    private static final String TAG = NonRootScanner.class.getSimpleName();

    private boolean waitingForVPNStart;

    private BroadcastReceiver vpnStateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (PacketCaptureService.BROADCAST_VPN_STATE.equals(intent.getAction()))
            {
                if (intent.getBooleanExtra("running", false))
                    waitingForVPNStart = false;
            }
        }
    };

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
                Snackbar.make(view, "Start clicked", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                startOrStopCapture();
            }
        });
        fabStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.StopClicked();
                //TODO stop listening here
                Snackbar.make(view, "Stop clicked", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                startOrStopCapture();
                Log.e("NonRootScanner MSG","Start Clicked");
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
            } else {
                Log.e(TAG,"Packet Capture Service - Stopped");
            }
        });
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

            Boolean isRunning = PacketCaptureService.isRunning();
            Intent i = isRunning
                    ? getServiceIntent(PacketCaptureService.ACTION_STOP)
                    : getServiceIntent(PacketCaptureService.ACTION_START);

            if (isRunning == Boolean.FALSE) {
                //  We can show UI here to allow user to specify filter criteria for the
                //  packet capturing
                //  TODO
                i.putExtra(PacketCaptureService.CAPTURE_NAME,   Utils.getUniqueTimestampName());
                i.putExtra(PacketCaptureService.PCF_PROTO_TYPE, "TCP/UDP/ICMP/HTTP/HTTPS");
                i.putExtra(PacketCaptureService.PCF_SRC_IP,     "");
                i.putExtra(PacketCaptureService.PCF_SRC_PORT,   "");
                i.putExtra(PacketCaptureService.PCF_DST_IP,     "");
                i.putExtra(PacketCaptureService.PCF_DST_PORT,   "");
            }

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
    public void connectWithPresenter()
    {
        presenter = new NonRootScannerPresenter(this);
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

        if (id == R.id.nav_non_root_packet_capture) {
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
}
