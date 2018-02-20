package com.app.whiff.whiff.RootScanner;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.os.Handler;
import android.widget.Toast;

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

    public static final String PARAM_IN_MESSAGE = "com.app.whiff.whiff.RootScanner.TCPdumpService.IMSG";
    public static final String PARAM_OUT_MESSAGE = "com.app.whiff.whiff.RootScanner.TCPdumpService.OMSG";

    private static final String TAG = TCPdumpService.class.getSimpleName();

    public static final String TCPdumpBinaryPath = "/data/data/com.app.whiff.whiff/files/";
    public static final String BROADCAST_TCPDUMP_STATE = "com.app.whiff.whiff.RootScanner.TCPdumpService.TCPDUMP_STATE";

    public TCPdumpService() {
        super("TCPdumpService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "TCPdumpService Started...", Toast.LENGTH_LONG).show();
            }
        });
        return super.onStartCommand(intent, flags, startID);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"Service stopped...", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String TCPdumpParams = intent.getStringExtra(ACTION_START);
        System.out.println("./tcpdump " + TCPdumpParams);
        Command command = new Command(0, "cd " + TCPdumpBinaryPath, "./tcpdump --list-interfaces", "./tcpdump " + TCPdumpParams) {
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

}
