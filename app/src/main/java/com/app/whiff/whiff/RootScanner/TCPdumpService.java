package com.app.whiff.whiff.RootScanner;

import android.app.Activity;
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
import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootShell.execution.Shell;
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

    public static final String NOTIFICATION = "com.app.whiff.whiff.RootScanner.UI.RootScanner";

    public static Command command, TCPdumpKillCommand;
    public boolean result;
    public boolean isShellRunning;


    public TCPdumpService() {
        super("TCPdumpService");
    }


    @Override
    public void onDestroy() {
        stopSniff();

        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"TCPdumpService stopping...", Toast.LENGTH_LONG).show();
            }
        });
        super.onDestroy();
    }

    public void stopSniff() {   // Will generate a warning, can be ignored safely.
        try {
            Shell.closeAll();
        } catch (IOException e) {
            e.printStackTrace();
        }

        TCPdumpKillCommand = new Command(0, "killall tcpdump");
        try {
            RootTools.getShell(true).add(TCPdumpKillCommand);
        } catch (IOException | TimeoutException | RootDeniedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Create notification to show that packets are being captured
        Notification.Builder builder = new Notification.Builder(getBaseContext())
                .setSmallIcon(R.drawable.ic_vpn)
                .setTicker("You are capturing packets.")
                .setContentTitle("Whiff is running . . .")
                .setContentText("You are capturing packets.")
                .setProgress(0, 0, true);

        startForeground(1, builder.build());


        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
<<<<<<< HEAD
                Toast.makeText(getApplicationContext(), "TCPdumpService started...", Toast.LENGTH_LONG).show();
=======
                Toast.makeText(getApplicationContext(), "TCPdumpService started...", Toast.LENGTH_SHORT).show();
>>>>>>> Thursday-Afternoon-Brunch
            }
        });

        isShellRunning = true;
        String TCPdumpParams = intent.getStringExtra(ACTION_START);
        System.out.println("TCPdumpParams = " + TCPdumpParams);

        command = new Command(0, "cd " + TCPdumpBinaryPath, "./tcpdump " + TCPdumpParams) {
            @Override
            public void commandOutput(int id, final String line) {
                RootShell.log("TCPdumpService" + line);
                super.commandOutput(id, line);
            }
            @Override
            public void commandTerminated(int id, String reason) {
                stopSniff();
                super.commandTerminated(id, reason);
            }
            @Override
            public void commandCompleted(int id, int exitcode) {
                super.commandCompleted(id, exitcode);
            }
        };
        try {
            RootTools.getShell(true).add(command);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (RootDeniedException e) {
            e.printStackTrace();
        }

        while (isShellRunning) {    // Prevent service from ending prematurely
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
