package com.app.whiff.whiff.HomePage;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.util.TypedValue;
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
import android.widget.Button;
import android.widget.Switch;

import com.app.whiff.whiff.ARPSpoofer.UI.ARPSpoofer;
import com.app.whiff.whiff.R;
import com.app.whiff.whiff.RootScanner.UI.RootScanner;
import com.app.whiff.whiff.NonRootScanner.UI.NonRootScanner;
import com.stericson.RootTools.RootTools;


public class HomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomePageViewInterface {

    // Buttons to launch other activities
    public Button RootScannerButton;
    public Button NonRootScannerButton;
    public Button ARPSpooferButton;
    public Button DisplayButton;
    public Switch NonRootSwitch;
    public Switch RootSwitch;
    public Switch ARPSpooferSwitch;

    // Start/Stop Button
    public FloatingActionButton fabStart;
    public FloatingActionButton fabStop;

    // Reference to presenter
    public HomePagePresenterInterface presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_page);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        connectWithPresenter(); // RootScannerPresenter object

        Context context = getApplicationContext();



        /*RootScannerButton = (Button) findViewById(R.id.RootScannerButton);
        RootScannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.RootScannerButtonClicked();
                Intent RootScannerActivity = new Intent(view.getContext(), RootScanner.class);
                startActivity(RootScannerActivity);
            }
        });

        NonRootScannerButton = (Button) findViewById(R.id.NonRootScannerButton);
        NonRootScannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.NonRootScannerButtonClicked();
                Intent NonRootScannerActivity = new Intent(view.getContext(), NonRootScanner.class);
                startActivity(NonRootScannerActivity);
            }
        });

        ARPSpooferButton = (Button) findViewById(R.id.ARPSpooferButton);
        ARPSpooferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.ARPSpooferButtonClicked();
                Intent ARPSpooferACtivity = new Intent(view.getContext(), ARPSpoofer.class);
                startActivity(ARPSpooferACtivity);
            }
        });*/


        DisplayButton = (Button) findViewById(R.id.DisplayButton);
        DisplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.DisplayButtonClicked();
                Intent DisplayButtonActivity = new Intent(view.getContext(), ARPSpoofer.class);
                startActivity(DisplayButtonActivity);
            }
        });

        RootSwitch = (Switch) findViewById(R.id.switch1);
        RootSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RootSwitch.isChecked()){
                    NonRootSwitch.setEnabled(false);
                }
                else if (ARPSpooferSwitch.isChecked()){
                    NonRootSwitch.setEnabled(false);
                }
                else{
                    NonRootSwitch.setEnabled(true);
                }
            }
        });

        NonRootSwitch = (Switch) findViewById(R.id.switch2);
        NonRootSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NonRootSwitch.isChecked()){
                    RootSwitch.setEnabled(false);
                    ARPSpooferSwitch.setEnabled(false);
                }
                else if(RootTools.isRootAvailable()){
                    RootSwitch.setEnabled(true);
                    ARPSpooferSwitch.setEnabled(true);
                }
                else{
                    RootSwitch.setEnabled(false);
                    ARPSpooferSwitch.setEnabled(false);
                }
            }
        });

        ARPSpooferSwitch = (Switch) findViewById(R.id.switch3);
        ARPSpooferSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ARPSpooferSwitch.isChecked()){
                    NonRootSwitch.setEnabled(false);
                }
                else if (RootSwitch.isChecked()){
                    NonRootSwitch.setEnabled(false);
                }
                else{
                    NonRootSwitch.setEnabled(true);
                }
            }
        });

        if(!(RootTools.isRootAvailable())){
            RootSwitch.setEnabled(false);
            ARPSpooferSwitch.setEnabled(false);
        }

        /*fabStart = (FloatingActionButton) findViewById(R.id.fab_start);
        fabStop = (FloatingActionButton) findViewById(R.id.fab_stop);
        fabStop.hide();
        fabStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.StartClicked();
                //TODO call packet listener here
                Snackbar.make(view, "Start clicked", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Log.d("RootScanner MSG","Start Clicked");
            }
        });

        fabStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.StopClicked();
                //TODO stop listening here
                Snackbar.make(view, "Stop clicked", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Log.d("RootScanner MSG","Stop Clicked");
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
    }

    public void connectWithPresenter() {
        // presenter = new RootScannerPresenter(this, handler);
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

        if (id == R.id.nav_root_packet_capture) {
            //TODO Create new activity
            Intent RootScannerActivity = new Intent(this, RootScanner.class);
            startActivity(RootScannerActivity);
        } else if (id == R.id.nav_non_root_packet_capture) {
            Intent NonRootScannerActivity = new Intent(this, NonRootScanner.class);
            startActivity(NonRootScannerActivity);
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
