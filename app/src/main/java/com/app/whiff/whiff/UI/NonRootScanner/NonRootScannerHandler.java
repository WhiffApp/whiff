package com.app.whiff.whiff.UI.NonRootScanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.VpnService;
import android.support.v4.content.LocalBroadcastManager;

import static android.app.Activity.RESULT_OK;
import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by danie on 30/1/2018.
 */

public class NonRootScannerHandler extends ContextWrapper {

    private static final int VPN_REQUEST_CODE = 0x0F;
    private boolean waitingForVPNStart;
    private Context context;

    private NonRootScannerHandler(Context base) {
        super(base);
        this.context = base;
    }


    public void StartVPNService() {
        waitingForVPNStart = false;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == VPN_REQUEST_CODE && resultCode == RESULT_OK)
        {
            waitingForVPNStart = true;
            Intent i = new Intent(this, PacketCaptureService.class);
            i.putExtra("CaptureName", Utils.getUniqueTimestampName());
            startService(i);
            // enableButton(false);
        }
    }

}
