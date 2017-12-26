package jp.co.taosoftware.android.packetcapture;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.VpnService;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import fyp.uow.whiff.R;
import io.reactivex.subjects.BehaviorSubject;

/**
 *
 *  This class is responsible for starting and stopping the packet capture activity.
 *  It uses the VpnService provided by Android to capture the data/control packets
 *  flowing in and out of the Android device without the need of device rooting.
 *
 *  This class and its namespace/package must not be changed because the
 *  native library methods are defined in this class using the JNI naming convention
 */

public class PacketCaptureService extends VpnService implements Handler.Callback, Runnable {

    public static final String ACTION_START = "fyp.uow.whiff.START";
    public static final String ACTION_STOP  = "fyp.uow.whiff.STOP";

    public static final String PCAP_LOG_FILENAME = "fyp.uow.whiff.pcap_log_filename";

    private Handler mHandler;

    private static String gTag;
    private static String mLogFileName;
    private PendingIntent mConfigureIntent;
    private Thread mThread;
    private ParcelFileDescriptor mParcelFileDescriptor;

    private static BehaviorSubject<Boolean> isRunning = BehaviorSubject.create();

    static {
        gTag = PacketCaptureService.class.getSimpleName();
        isRunning.onNext(Boolean.FALSE);    // Not running initially
        System.loadLibrary("tPacketCapture");
    }

    public static boolean isRunning(){
        return isRunning.getValue();
    }

    public static BehaviorSubject<Boolean> getIsRunning(){
        return isRunning;
    }

    private final String getTag() {
        return gTag;
    }

    @Override
    public void onCreate() {
        // The handler is only used to show Toast messages.
        if (mHandler == null) {
            mHandler = new Handler(this);
        }

        // Create the intent to "configure" the connection.
        mConfigureIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, PacketCaptureService.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onDestroy() {
        if (this.mThread != null) {
            this.mThread.interrupt();
        }
    }

    public void onRevoke() {
        stopPacketCapture();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) return START_REDELIVER_INTENT;

        String action = intent.getAction();
        if ( ACTION_START.equals(action) ) {

            startPacketCapture(intent);
            return START_STICKY;

        } else if ( ACTION_STOP.equals(intent.getAction()) ) {

            stopPacketCapture();
            return START_NOT_STICKY;

        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public boolean handleMessage(Message message) {
        Toast.makeText(this, message.what, Toast.LENGTH_SHORT).show();
        if (message.what != R.string.disconnected) {
            updateForegroundNotification(message.what);
        }
        return true;
    }

    @Override
    public synchronized void run() {

        try {
            // Become a foreground service. Background services can be VPN services too, but they can
            // be killed by background check before getting a chance to receive onRevoke().
            updateForegroundNotification(R.string.connected);
            mHandler.sendEmptyMessage(R.string.connected);

            // Kick off a connection.
            setUidCaptureStatus(false);
            setPCapFileName(mLogFileName);
            Builder builder = new Builder();
            builder.addAddress("10.5.0.1", 32);
            builder.addRoute("0.0.0.0", 0);
            builder.setSession("tPacketCapture");
            builder.setConfigureIntent(mConfigureIntent);
            this.mParcelFileDescriptor = builder.establish();

            //  This method is blocking until stopCapture is called.
            startCapture(this.mParcelFileDescriptor.getFd());

            mHandler.sendEmptyMessage(R.string.disconnecting);
            this.mParcelFileDescriptor.close();

        } catch(Exception ex) {
            Log.e(getTag(), "Exception", ex);

        } catch (Throwable ex) {
            //  Most likely is the java.lang.UnsatisfiedLinkError
            Log.e(getTag(), "Throwable", ex);
        }
        this.mParcelFileDescriptor = null;
    }

    private void startPacketCapture(Intent intent) {
        mLogFileName = intent.getStringExtra(PCAP_LOG_FILENAME);
        this.mThread = new Thread(this, "PacketSnifferThread");
        this.mThread.start();

        //  Notify the application the Packet Capture process is started.
        isRunning.onNext(Boolean.TRUE);
    }

    private void stopPacketCapture() {
        stopSelf();
        stopCapture();
        mHandler.sendEmptyMessage(R.string.disconnected);
        stopForeground(true);

        //  Notify the application the Packet Capture process is stopped.
        isRunning.onNext(Boolean.FALSE);
    }

    private void updateForegroundNotification(final int message) {
        startForeground(1, new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_vpn)
                .setContentText(getString(message))
                .setContentIntent(mConfigureIntent)
                .build());
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    //
    // JNI methods defined in the libtPacketCapture.so
    //
    ///////////////////////////////////////////////////////////////////////////////////////////

    //  Java_jp_co_taosoftware_android_packetcapture_insertUid
    private native void insertUid(int i);

    //  Java_jp_co_taosoftware_android_packetcapture_setUidCaptureStatus
    private native void setUidCaptureStatus(boolean z);

    //  Java_jp_co_taosoftware_android_packetcapture_setPCapFileName
    private native void setPCapFileName(String str);

    //  Java_jp_co_taosoftware_android_packetcapture_startCapture
    private native void startCapture(int i);

    //  Java_jp_co_taosoftware_android_packetcapture_stopCapture
    private native void stopCapture();
}
