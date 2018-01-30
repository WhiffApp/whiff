package com.app.whiff.whiff.UI.NonRootScanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.VpnService;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.app.whiff.whiff.R;
import com.app.whiff.whiff.UI.HomePage.HomePagePresenter;
import com.app.whiff.whiff.UI.HomePage.HomePagePresenterInterface;
import com.app.whiff.whiff.UI.HomePage.HomePageViewInterface;
import com.app.whiff.whiff.UI.HomePage.WEPCrack;


public class NonRootScanner extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NonRootScannerViewInterface {

    public Button vpnButton;
    public NonRootScannerPresenterInterface presenter;


    public TextView TV1;
    public FloatingActionButton fabStart;
    public FloatingActionButton fabStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
         * Do not call su from main thread.
         * Please see: https://su.chainfire.eu/
         */
//        try {
//            Runtime.getRuntime().exec("su");
//        } catch (IOException e) {}
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_non_root_scanner);

        vpnButton = (Button)findViewById(R.id.vpn);
        vpnButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });
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
        // presenter = new HomePagePresenter(this, handler);
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

        if (id == R.id.nav_Packet_Capture) {
            //TODO Create new activity
        } else if (id == R.id.nav_wep_crack) {
            Intent WEPActivity = new Intent (this, WEPCrack.class);
            startActivity(WEPActivity);
        } else if (id == R.id.nav_Import_File) {

        } else if (id == R.id.nav_help_faq) {
            Intent helpActivity = new Intent (this, com.app.whiff.whiff.help.class);
            startActivity(helpActivity);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
