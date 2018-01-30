package com.app.whiff.whiff.UI.NonRootScanner;

import android.content.Intent;

import static android.app.Activity.RESULT_OK;

/**
 * Created by danie on 30/1/2018.
 */

public class NonRootScannerHandler {
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VPN_REQUEST_CODE && resultCode == RESULT_OK)
        {
            waitingForVPNStart = true;
            Intent i = new Intent(this, PacketCaptureService.class);
            i.putExtra("CaptureName", Utils.getUniqueTimestampName());
            startService(i);
            enableButton(false);
        }
    }
}
