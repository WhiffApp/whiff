package com.app.whiff.whiff.UI.View;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.app.whiff.whiff.NonRootScanner.Capture;
import com.app.whiff.whiff.NonRootScanner.CaptureDAO;
import com.app.whiff.whiff.NonRootScanner.FileManager;
import com.app.whiff.whiff.NonRootScanner.PacketCaptureService;
import com.app.whiff.whiff.NonRootScanner.PacketContentFilter;
import com.app.whiff.whiff.NonRootScanner.Protocols;
import com.app.whiff.whiff.R;
import com.app.whiff.whiff.UI.PacketDbContent.PacketDbContentPage;
import com.stericson.RootTools.RootTools;

import java.util.List;


public class ViewPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ViewPageViewInterface,
        ViewPageRecyclerViewAdapter.ItemClickListener {

    public ViewPagePresenterInterface mPresenter;
    private static final int VPN_REQUEST_CODE = 0x0F;
    private static final String TAG = ViewPage.class.getSimpleName();

    public FloatingActionButton fabImport;

    RecyclerView mRecyclerView;
    ViewPageRecyclerViewAdapter mAdapter;

    private Dialog mFilterDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        connectWithPresenter();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu nav_Menu = navigationView.getMenu();
        if (!RootTools.isAccessGiven() || !RootTools.isBusyboxAvailable()) {
            nav_Menu.findItem(R.id.nav_root_packet_capture).setVisible(false);
        }
        nav_Menu.findItem(R.id.nav_Import_File).setVisible(false);

        fabImport = findViewById(R.id.fab_import);
        fabImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(),
                        com.app.whiff.whiff.UI.ImportPacketFile.ImportPacketFilePage.class);
                startActivityForResult(i, 1);
                refreshList();
            }
        });

        FileManager.init();

        // set up the RecyclerView
        mRecyclerView = findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshList();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        mFilterDialog = builder.setTitle(R.string.title_packet_filter)
                .setView(R.layout.dialog_packet_filter)
                .setPositiveButton(R.string.dialog_apply_button,  applyButtonOnClickListener)
                .setNegativeButton(R.string.dialog_cancel_button, cancelButtonOnClickListener)
                .create();
    }

    public void connectWithPresenter()
    {
        mPresenter = new ViewPagePresenter(this,
                new CaptureDAO(this.getApplicationContext()));
    }

    private Intent getServiceIntent(String action) {
        Intent i = new Intent( this, PacketCaptureService.class);
        i.setAction(action);

        return i;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            refreshList();
        }
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
            onOptionItemFilterClicked();
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

    private void refreshList() {

        if (mAdapter != null) {
            mAdapter.setClickListener(null);
            mAdapter = null;
        }

        List<Capture> files = mPresenter.getCaptureList();
        mAdapter = new ViewPageRecyclerViewAdapter(this, files);
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

        Bundle extras = new Bundle();
        extras.putParcelable("PACKET_CONTENT_FILTER", mPresenter.getPacketContentFilter());
        i.putExtras(extras);

        startActivity(i);
    }

    private DialogInterface.OnClickListener applyButtonOnClickListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {

            PacketContentFilter filter = mPresenter.getPacketContentFilter();

            //  Protocol
            CheckBox check_tcp = mFilterDialog.findViewById(R.id.check_tcp);
            filter.protocols.remove(Protocols.Tcp);
            if (check_tcp.isChecked()) {
                filter.protocols.add(Protocols.Tcp);
            }

            CheckBox check_udp = mFilterDialog.findViewById(R.id.check_udp);
            filter.protocols.remove(Protocols.Udp);
            if (check_udp.isChecked()) {
                filter.protocols.add(Protocols.Udp);
            }

            CheckBox check_http = mFilterDialog.findViewById(R.id.check_http);
            filter.protocols.remove(Protocols.Http);
            if (check_http.isChecked()) {
                filter.protocols.add(Protocols.Http);
            }

            //  Source IP
            CheckBox check_sip = mFilterDialog.findViewById(R.id.check_sip);
            if (check_sip.isChecked()) {
                EditText edit_sip = mFilterDialog.findViewById(R.id.edit_sip);
                String sip = edit_sip.getText().toString();
                if (sip != null && sip != "") {
                    filter.sourceIP = sip;
                }
                else {
                    check_sip.setChecked(Boolean.FALSE);
                }
            } else {
                filter.sourceIP = null;
            }

            //  Destination IP
            CheckBox check_dip = mFilterDialog.findViewById(R.id.check_dip);
            if (check_dip.isChecked()) {
                EditText edit_dip = mFilterDialog.findViewById(R.id.edit_dip);
                String dip = edit_dip.getText().toString();
                if (dip != null && dip != "") {
                    filter.destinationIP = dip;
                }
                else {
                    check_dip.setChecked(Boolean.FALSE);
                }
            } else {
                filter.destinationIP = null;
            }

            //  Length
            CheckBox check_length = mFilterDialog.findViewById(R.id.check_length);
            if (check_length.isChecked()) {
                EditText edit_length = mFilterDialog.findViewById(R.id.edit_length);
                String length = edit_length.getText().toString();

                try {
                    if (length != null && length != "" && Long.valueOf(length) > 0) {
                        filter.length = Long.valueOf(length);
                    }
                    else {
                        edit_length.setText("");
                        check_length.setChecked(Boolean.FALSE);
                    }
                } catch(Exception e) {
                    edit_length.setText("");
                    check_length.setChecked(Boolean.FALSE);
                }
            } else {
                filter.length = 0L;
            }

            mFilterDialog.dismiss();
        }
    };

    private DialogInterface.OnClickListener cancelButtonOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.cancel();
        }
    };

    private void onOptionItemFilterClicked() {

        initPacketContentFilterDialog(mFilterDialog, mPresenter.getPacketContentFilter());
        mFilterDialog.show();
    }

    private void initPacketContentFilterDialog(Dialog dialog, PacketContentFilter filter) {

        if (filter.protocols != null && filter.protocols.size() > 0) {

            for(String protocol : filter.protocols) {

                if (protocol == Protocols.Tcp) {
                    CheckBox check_tcp = dialog.findViewById(R.id.check_tcp);
                    check_tcp.setChecked(Boolean.TRUE);
                }

                if (protocol == Protocols.Udp) {
                    CheckBox check_udp = dialog.findViewById(R.id.check_udp);
                    check_udp.setChecked(Boolean.TRUE);
                }

                if (protocol == Protocols.Http) {
                    CheckBox check_http = dialog.findViewById(R.id.check_http);
                    check_http.setChecked(Boolean.TRUE);
                }
            }
        }

        //  Source IP
        if (filter.sourceIP != null && filter.sourceIP != "") {

            EditText edit_sip = dialog.findViewById(R.id.edit_sip);
            edit_sip.setText(filter.sourceIP);

            CheckBox check_sip = dialog.findViewById(R.id.check_sip);
            check_sip.setChecked(Boolean.TRUE);
        }

        //  Destination IP
        if (filter.destinationIP != null && filter.destinationIP != "") {

            EditText edit_dip = dialog.findViewById(R.id.edit_dip);
            edit_dip.setText(filter.destinationIP);

            CheckBox check_dip = dialog.findViewById(R.id.check_dip);
            check_dip.setChecked(Boolean.TRUE);
        }

        //  Length
        if (filter.length > 0L) {
            EditText edit_length = dialog.findViewById(R.id.edit_length);
            edit_length.setText(filter.length.toString());

            CheckBox check_length = dialog.findViewById(R.id.check_length);
            check_length.setChecked(Boolean.TRUE);
        }
    }

}
