package com.app.whiff.whiff.HomePage;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.app.whiff.whiff.R;

import java.io.IOException;

public class WEPCrack extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            Runtime.getRuntime().exec("su");
        } catch (IOException e) {}
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wepcrack);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
         * Jnetpcap function to get all devices
         * Does not work on Android
         * Because no network interfaces will be found.
         */
//        List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs
//        StringBuilder errbuf = new StringBuilder();     // For any error msgs
//
//        int r = Pcap.findAllDevs(alldevs, errbuf);
//        if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
//            System.err.printf("Can't read list of devices, error is %s", errbuf.toString());
//            Log.i("Can't read list of devices, error is %s", errbuf.toString());
//            return;
//      }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


}
