package com.app.whiff.whiff.ARPSpoofer;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.format.Formatter;

import java.net.InetAddress;
import java.util.Collections;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ARPSpoofService extends IntentService {
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


    public ARPSpoofService() {
        super("ARPSpoofService");
    }

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
    }

    @Override
    protected void onHandleIntent(Intent intent) {
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
            }
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
