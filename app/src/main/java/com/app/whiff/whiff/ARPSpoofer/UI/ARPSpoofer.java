package com.app.whiff.whiff.ARPSpoofer.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.app.whiff.whiff.NonRootScanner.UI.NonRootScanner;
import com.app.whiff.whiff.R;


public class ARPSpoofer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ARPSpooferViewInterface {

    public TextView TV1;
    public FloatingActionButton fabStart;
    public FloatingActionButton fabStop;
    public ARPSpooferPresenterInterface presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_root_scanner);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        connectWithPresenter(); // ARPSpooferPresenter object

        Context context = getApplicationContext();

        TV1 = (TextView) findViewById(R.id.TV1);
        fabStart = (FloatingActionButton) findViewById(R.id.fab_start);
        fabStop = (FloatingActionButton) findViewById(R.id.fab_stop);
        fabStop.hide();
        fabStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.StartClicked();
                //TODO call packet listener here
                Snackbar.make(view, "Start clicked", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Log.d("ARPSpoofer MSG","Start Clicked");
            }
        });
        fabStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.StopClicked();
                //TODO stop listening here
                Snackbar.make(view, "Stop clicked", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Log.d("ARPSpoofer MSG","Stop Clicked");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        presenter.ActivityStarted();
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
        // presenter = new ARPSpooferPresenter(this, handler);
        presenter = new ARPSpooferPresenter(this);
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

        if (id == R.id.nav_root_packet_capture) {
            //TODO Create new activity
        } else if (id == R.id.nav_non_root_packet_capture) {
            Intent RootScannerActivity = new Intent(this, NonRootScanner.class);
            startActivity(RootScannerActivity);
        } else if (id == R.id.nav_wep_crack) {
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
