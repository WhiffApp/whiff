package com.app.whiff.whiff.ARPSpoofer.UI;

<<<<<<< HEAD:app/src/main/java/com/app/whiff/whiff/ARPSpoofer/UI/ARPSpoofer.java
import android.content.Context;
import android.content.Intent;
=======
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
>>>>>>> Thursday-Afternoon-Brunch:app/src/main/java/com/app/whiff/whiff/UI/HomePage/HomePage.java
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
<<<<<<< HEAD:app/src/main/java/com/app/whiff/whiff/ARPSpoofer/UI/ARPSpoofer.java
import android.view.View;
import android.widget.TextView;

import com.app.whiff.whiff.NonRootScanner.UI.NonRootScanner;
=======
import android.content.Intent;
import android.widget.Button;

import com.app.whiff.whiff.ARPSpoofer.UI.ARPSpoofer;
import com.app.whiff.whiff.NonRootScanner.FileManager;
>>>>>>> Thursday-Afternoon-Brunch:app/src/main/java/com/app/whiff/whiff/UI/HomePage/HomePage.java
import com.app.whiff.whiff.R;
import com.app.whiff.whiff.RootScanner.UI.RootScanner;
import com.app.whiff.whiff.NonRootScanner.UI.NonRootScanner;
import com.stericson.RootTools.RootTools;

import static com.stericson.RootShell.RootShell.isAccessGiven;


<<<<<<< HEAD:app/src/main/java/com/app/whiff/whiff/ARPSpoofer/UI/ARPSpoofer.java
public class ARPSpoofer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ARPSpooferViewInterface {

    public TextView TV1;
    public FloatingActionButton fabStart;
    public FloatingActionButton fabStop;
    public ARPSpooferPresenterInterface presenter;
=======
public class HomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomePageViewInterface {

    private Button ARPSpooferButton;

    // Reference to presenter
    public HomePagePresenterInterface presenter;
>>>>>>> Thursday-Afternoon-Brunch:app/src/main/java/com/app/whiff/whiff/UI/HomePage/HomePage.java

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

<<<<<<< HEAD:app/src/main/java/com/app/whiff/whiff/ARPSpoofer/UI/ARPSpoofer.java
        setContentView(R.layout.activity_root_scanner);
=======
        setContentView(R.layout.activity_home_page);
>>>>>>> Thursday-Afternoon-Brunch:app/src/main/java/com/app/whiff/whiff/UI/HomePage/HomePage.java

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

<<<<<<< HEAD:app/src/main/java/com/app/whiff/whiff/ARPSpoofer/UI/ARPSpoofer.java
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
=======
        connectWithPresenter(); // RootScannerPresenter object

        Context context = getApplicationContext();


        Button rootScannerButton = (Button) findViewById(R.id.RootScannerButton);
        if (!RootTools.isAccessGiven()) {
            rootScannerButton.setVisibility(View.GONE);
        } else{
            rootScannerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent RootScannerActivity = new Intent(view.getContext(), RootScanner.class);
                    startActivity(RootScannerActivity);
                }
            });
        }

        Button nonRootScannerButton = (Button) findViewById(R.id.NonRootScannerButton);
        nonRootScannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent NonRootScannerActivity = new Intent(view.getContext(), com.app.whiff.whiff.NonRootScanner.UI.NonRootScanner.class);
                startActivity(NonRootScannerActivity);
            }
        });

        Button transportProtocolButton = (Button) findViewById(R.id.NonRootScannerTransportButton);
        transportProtocolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent TransportProtocolActivity = new Intent(view.getContext(), com.app.whiff.whiff.UI.PacketDb.PacketDbPage.class);
                startActivity(TransportProtocolActivity);
>>>>>>> Thursday-Afternoon-Brunch:app/src/main/java/com/app/whiff/whiff/UI/HomePage/HomePage.java
            }
        });

        Button importButton = (Button) findViewById(R.id.ImportButton);
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ImportIntent = new Intent (view.getContext(),
                        com.app.whiff.whiff.UI.View.ViewPage.class);
                startActivity(ImportIntent);
            }
        });

        Button helpFaqButton = (Button) findViewById(R.id.HelpButton);
        helpFaqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
<<<<<<< HEAD:app/src/main/java/com/app/whiff/whiff/ARPSpoofer/UI/ARPSpoofer.java
                presenter.StopClicked();
                //TODO stop listening here
                Snackbar.make(view, "Stop clicked", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Log.d("ARPSpoofer MSG","Stop Clicked");
=======
                Intent browserIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://whiffuow.wixsite.com/home"));
                startActivity(browserIntent);
>>>>>>> Thursday-Afternoon-Brunch:app/src/main/java/com/app/whiff/whiff/UI/HomePage/HomePage.java
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
<<<<<<< HEAD:app/src/main/java/com/app/whiff/whiff/ARPSpoofer/UI/ARPSpoofer.java

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

=======
        Menu nav_Menu = navigationView.getMenu();
        if (!RootTools.isAccessGiven()) {
            nav_Menu.findItem(R.id.nav_root_packet_capture).setVisible(false);
        }
    }

    public void showMessage(String message) {
    }

    public void connectWithPresenter() {
        presenter = new HomePagePresenter(this);
    }

>>>>>>> Thursday-Afternoon-Brunch:app/src/main/java/com/app/whiff/whiff/UI/HomePage/HomePage.java
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
<<<<<<< HEAD:app/src/main/java/com/app/whiff/whiff/ARPSpoofer/UI/ARPSpoofer.java
            //TODO Create new activity
        } else if (id == R.id.nav_non_root_packet_capture) {
            Intent RootScannerActivity = new Intent(this, NonRootScanner.class);
            startActivity(RootScannerActivity);
        } else if (id == R.id.nav_wep_crack) {
        } else if (id == R.id.nav_Import_File) {

        } else if (id == R.id.nav_help_faq) {
            Intent helpActivity = new Intent (this, com.app.whiff.whiff.help.class);
            startActivity(helpActivity);
=======
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
                    com.app.whiff.whiff.UI.ImportPacketFile.ImportPacketFilePage.class);
            startActivity(ImportActivity);

        } else if (id == R.id.nav_help_faq) {
            Intent browserIntent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://whiffuow.wixsite.com/home"));
            startActivity(browserIntent);
>>>>>>> Thursday-Afternoon-Brunch:app/src/main/java/com/app/whiff/whiff/UI/HomePage/HomePage.java
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
