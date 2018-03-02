package com.app.whiff.whiff.NonRootScanner.UI;

import android.net.Uri;
import android.os.Bundle;
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

import com.app.whiff.whiff.R;

public class NonRootScanner extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NonRootScannerViewInterface {

    private static final String TAG = NonRootScanner.class.getSimpleName();
    public NonRootScannerPresenterInterface mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_root_scanner);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        connectWithPresenter();

        Button packetFileButton = findViewById(R.id.non_root_scanner_file_button);
        packetFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent PacketFileActivity = new Intent(view.getContext(),
                        com.app.whiff.whiff.UI.PacketFile.PacketFilePage.class);
                startActivity(PacketFileActivity);
            }
        });

        Button packetDbButton = findViewById(R.id.non_root_scanner_db_button);
        packetDbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent PacketDbActivity = new Intent(view.getContext(),
                        com.app.whiff.whiff.UI.PacketDb.PacketDbPage.class);
                startActivity(PacketDbActivity);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void connectWithPresenter()
    {
        mPresenter = new NonRootScannerPresenter(this);
    }

    @Override
    public void onBackPressed() {
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_root_packet_capture) {
            Intent RootScannerActivity = new Intent(this,
                    com.app.whiff.whiff.RootScanner.UI.RootScanner.class);
            startActivity(RootScannerActivity);

        } else if (id == R.id.nav_non_root_packet_capture) {
            Intent NonRootScannerActivity = new Intent(this,
                    com.app.whiff.whiff.NonRootScanner.UI.NonRootScanner.class);
            startActivity(NonRootScannerActivity);

        } else if (id == R.id.nav_non_root_sniffer_transport) {
            Intent NonRootTransportActivity = new Intent(this,
                    com.app.whiff.whiff.UI.PacketDb.PacketDbPage.class);
            startActivity(NonRootTransportActivity);

        } else if (id == R.id.nav_Import_File) {
            Intent ImportActivity = new Intent (this,
                    com.app.whiff.whiff.UI.View.ViewPage.class);
            startActivity(ImportActivity);

        } else if (id == R.id.nav_help_faq) {
            Intent browserIntent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://whiffuow.wixsite.com/home"));
            startActivity(browserIntent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
