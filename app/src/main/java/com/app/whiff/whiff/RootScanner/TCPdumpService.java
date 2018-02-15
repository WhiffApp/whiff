package com.app.whiff.whiff.RootScanner;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.app.whiff.whiff.R;
import com.app.whiff.whiff.RootScanner.UI.RootScanner;
import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootTools.RootTools;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import io.reactivex.subjects.BehaviorSubject;

public class TCPdumpService extends IntentService {

    public static final String ACTION_START = "com.app.whiff.whiff.RootScanner.TCPdumpService.START";
    public static final String ACTION_STOP  = "com.app.whiff.whiff.RootScanner.TCPdumpService.STOP";

    public static final String PARAM_IN_MESSAGE = "imsg";
    public static final String PARAM_OUT_MESSAGE = "omsg";

    private static final String TAG = TCPdumpService.class.getSimpleName();

    public static final String TCPdumpBinaryPath = "/data/data/com.app.whiff.whiff/files/";

    private PendingIntent pendingIntent;

    private Command command;

    private static BehaviorSubject<Boolean> isRunning = BehaviorSubject.create();

    static {
        isRunning.onNext(Boolean.FALSE);    // Not running initially
    }

    public static boolean isRunning(){
        return isRunning.getValue();
    }

    public static BehaviorSubject<Boolean> getIsRunning(){
        return isRunning;
    }

    public TCPdumpService() {
        super("TCPdumpService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("TCPdumpService.onHandleIntent", "onHandleIntent started");
        String TCPdumpParams = intent.getStringExtra(ACTION_START);

        startPacketCapture(intent, TCPdumpParams);

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(RootScanner.ResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        String result = "OK";
        broadcastIntent.putExtra(PARAM_OUT_MESSAGE, result);
        sendBroadcast(broadcastIntent);
    }

    private void startPacketCapture(Intent intent, final String TCPdumpParams) {
        command = new Command(0, "cd " + TCPdumpBinaryPath, "./tcpdump " + TCPdumpParams + "test.pcap") {
            // Command command = new Command(0, "cd " + TCPdumpBinaryPath, "./tcpdump --list-interfaces") {
            // Command command = new Command(0, "tcpdump -i wlan0 -vvv") {
            @Override
            public void commandOutput(int id, final String line) {
                System.out.println(line);
                super.commandOutput(id, line);
            }

            @Override
            public void commandTerminated(int id, String reason) {
                System.out.println(reason);
                super.commandTerminated(id, reason);
            }

            @Override
            public void commandCompleted(int id, int exitcode) {
                System.out.println(exitcode);
                super.commandCompleted(id, exitcode);
            }
        };
        try {
            RootTools.getShell(true).add(command);
        } catch (IOException | RootDeniedException | TimeoutException e) {
            e.printStackTrace();
        }

    }

    private void stopPacketCapture() {
        stopSelf();
        command.terminate();
    }

    private void updateForegroundNotification(final int message) {
        startForeground(1, new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_vpn)
                .setContentText(getString(message))
                .setContentIntent(pendingIntent)
                .build());
    }
}
