package com.app.whiff.whiff.UI.NonRootScanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.VpnService;
import android.support.v4.content.LocalBroadcastManager;

import static android.app.Activity.RESULT_OK;
import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by danie on 30/1/2018.
 */

public class NonRootScannerHandler {

    private static final int VPN_REQUEST_CODE = 0x0F;
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

    public void StartVPNService() {
        waitingForVPNStart = false;
        LocalBroadcastManager.getInstance(this).registerReceiver(vpnStateReceiver,
                new IntentFilter(PacketCaptureService.BROADCAST_VPN_STATE));

        Intent vpnIntent = VpnService.prepare(this);
        if (vpnIntent != null)
            startActivityForResult(vpnIntent, VPN_REQUEST_CODE);
        else
            onActivityResult(VPN_REQUEST_CODE, RESULT_OK, null);

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
