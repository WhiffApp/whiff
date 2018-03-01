package com.app.whiff.whiff.ARPSpoofer;

import android.app.IntentService;
<<<<<<< HEAD
import android.content.Intent;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.format.Formatter;

import java.net.InetAddress;
import java.util.Collections;
=======
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

>>>>>>> Thursday-Afternoon-Brunch

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ARPSpoofService extends IntentService {
<<<<<<< HEAD
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.app.whiff.whiff.action.FOO";
    private static final String ACTION_BAZ = "com.app.whiff.whiff.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.app.whiff.whiff.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.app.whiff.whiff.extra.PARAM2";


    private final 			String IPV4_FILEPATH = "/proc/sys/net/ipv4/ip_forward";

	private final 			String IPTABLES_CLEAR_NAT 	= "iptables -t nat -F";
	private final 			String IPTABLES_CLEAR		= "iptables -F";
	private final 			String IPTABLES_POSTROUTE   = "iptables -t nat -I POSTROUTING -s 0/0 -j MASQUERADE";
	private final 			String IPTABLES_ACCEPT_ALL  = "iptables -P FORWARD ACCEPT";

	private static final 	String TAG = "ArpspoofService";
	private volatile 		Thread myThread;
	private static volatile WifiManager.WifiLock wifiLock;
	private static volatile PowerManager.WakeLock wakeLock;

=======

    public static final String ACTION_START = "com.app.whiff.whiff.RootScanner.ARPSpoofService.START";
    public static final String arpspoofBinaryPath = "/data/data/com.app.whiff.whiff/files/";
    public Context context;
    private static Command command, ARPSpoofKillCommand;
    private boolean isShellRunning;
>>>>>>> Thursday-Afternoon-Brunch

    public ARPSpoofService() {
        super("ARPSpoofService");
    }

<<<<<<< HEAD
    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, ARPSpoofService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, ARPSpoofService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
=======
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

>>>>>>> Thursday-Afternoon-Brunch
    }

    @Override
    protected void onHandleIntent(Intent intent) {
<<<<<<< HEAD
        if (intent != null) {
            final String action = intent.getAction();
            Bundle bundle = intent.getExtras();
            String localBin = bundle.getString("localBin");
            String gateway = bundle.getString("gateway");
            String wifiInterface = bundle.getString("interface");
            final String command = localBin + " -s 1 -i " + wifiInterface + " " + gateway;

            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                // handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
=======
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
>>>>>>> Thursday-Afternoon-Brunch
            }
        }
    }

<<<<<<< HEAD
    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
=======
    public void setIPForwarding(boolean truth) {
        Command c = new Command(0, "echo " + truth + " > /proc/sys/net/ipv4/ip_forward");
        try {
            RootTools.getShell(true).add(c);
        } catch (IOException | TimeoutException | RootDeniedException e) {
            e.printStackTrace();
        }
    }


>>>>>>> Thursday-Afternoon-Brunch
}
