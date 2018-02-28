package com.app.whiff.whiff.ARPSpoofer;

import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.app.whiff.whiff.R;
import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootShell.execution.Shell;
import com.stericson.RootTools.RootTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ARPSpoofService extends IntentService {

    public static final String ACTION_START = "com.app.whiff.whiff.RootScanner.ARPSpoofService.START";
    public static final String arpspoofBinaryPath = "/data/data/com.app.whiff.whiff/files/";
    public Context context;
    private static Command command, ARPSpoofKillCommand;
    private boolean isShellRunning;

    public ARPSpoofService() {
        super("ARPSpoofService");
    }

    @Override
    public void onDestroy() {
        Log.d("ARPSpoofService", "onDestroy called");
        stopSpoof();
        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"ARPSpoofService stopping...", Toast.LENGTH_SHORT).show();
            }
        });
        super.onDestroy();
    }

    public void stopSpoof() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("ARPSpoof", "stopspoof");
                ARPSpoofKillCommand = new Command(0, "killall arpspoof") {
                    @Override
                    public void commandOutput(int id, final String line) {
                        Log.d("ARPSpoofService", line);
                        super.commandOutput(id, line);
                    }

                    @Override
                    public void commandTerminated(int id, String reason) {
                        System.out.println(reason);
                        super.commandTerminated(id, reason);
                    }

                    @Override
                    public void commandCompleted(int id, int exitcode) {
                        super.commandCompleted(id, exitcode);
                    }
                };
                try {
                    RootTools.getShell(true).add(ARPSpoofKillCommand);
                } catch (IOException | RootDeniedException | TimeoutException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Create notification to show that packets are being captured
        Notification.Builder builder = new Notification.Builder(getBaseContext())
                .setSmallIcon(R.drawable.ic_vpn)
                .setTicker("You are spoofing ARP packets.")
                .setContentTitle("Whiff is running . . .")
                .setContentText("You are spoofing ARP packets.")
                .setProgress(0, 0, true);

        startForeground(1, builder.build());

        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "ARPSpoofService started...", Toast.LENGTH_SHORT).show();
            }
        });

        isShellRunning = true;
        String arpspoofParams = intent.getStringExtra(ARPSpoofService.ACTION_START);

        final String[] s = new String[3];
        s[0] = "su";
        s[1] = "cd /data/data/com.app.whiff.whiff/files";
        s[2] = "./arpspoof 192.168.1.254";

        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(s);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String temp;
            while ((temp = stdInput.readLine()) != null) {
                System.out.println(temp);
            }
        } catch (IOException e) {}

        while (isShellRunning) {    // Prevent service from ending prematurely
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setIPForwarding(boolean truth) {
        Command c = new Command(0, "echo " + truth + " > /proc/sys/net/ipv4/ip_forward");
        try {
            RootTools.getShell(true).add(c);
        } catch (IOException | TimeoutException | RootDeniedException e) {
            e.printStackTrace();
        }
    }


}
