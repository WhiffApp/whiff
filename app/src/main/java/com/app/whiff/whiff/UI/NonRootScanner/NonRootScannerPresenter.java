package com.app.whiff.whiff.UI.NonRootScanner;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * NonRootScannerPresenter
 * Actions to take when buttons are clicked in NonRootScanner activity.
 */

public class NonRootScannerPresenter implements NonRootScannerPresenterInterface {

    public Context context;
    public NonRootScannerViewInterface view;
    public String mLine = "";      // Message from handler

    private static final int VPN_REQUEST_CODE = 0x0F;
    private boolean waitingForVPNStart;
    // private NonRootScannerHandler nonRootScannerHandler = new NonRootScannerHandler(this);
    private PacketCaptureService packetCaptureService = new PacketCaptureService();

    // Receives the state of VPN in Android control panel
    private BroadcastReceiver vpnStateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (packetCaptureService.BROADCAST_VPN_STATE.equals(intent.getAction()))
            {
                if (intent.getBooleanExtra("running", false))
                    waitingForVPNStart = false;
            }
        }
    };

    // Handler that handles messages
    @SuppressLint("HandlerLeak")
    Handler TCPdumpHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            Bundle bundle = message.getData();
            String text = bundle.getString("key");

            if (mLine != null && !mLine.isEmpty())
                mLine = mLine + "\n" + text;
            else
                mLine = text;

            view.showMessage(mLine);

        }
    };

    public NonRootScannerPresenter(NonRootScanner nonRootScanner) {
        view = nonRootScanner;
        context = nonRootScanner;
        // tcpdump = new TCPdump(nonRootScanner, TCPdumpHandler);
    }

    public void VpnButtonClicked() {
    }

    public void StartClicked() {
        view.hideFabStart();
        // Start VPNService

    }

    public void StopClicked() {
        view.hideFabStop();
        // Stop VPNService
    }

}
