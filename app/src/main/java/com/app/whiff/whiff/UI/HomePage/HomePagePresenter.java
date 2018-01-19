package com.app.whiff.whiff.UI.HomePage;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.app.whiff.whiff.R;
import com.app.whiff.whiff.TCPdump;
import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootTools.RootTools;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by gyych on 1/1/2018.
 * HomePagePresenter
 * Actions to take when buttons are clicked in HomePage activity.
 */

public class HomePagePresenter implements HomePagePresenterInterface {

    public Context context;
    public HomePageViewInterface view;
    public String message;
    public Handler handler;
    public String mLine = "";

    public HomePagePresenter(HomePage homepage, Handler handler) {
        view = homepage;
        context = homepage;
        this.handler = handler;
    }

    public void StartClicked() {
        view.hideFabStart();

        // Begin packet capture

        // 1. Start new thread so that UI is not blocked by TCPdump and su calls
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (Looper.myLooper() == Looper.getMainLooper()) {
                    Log.d("HomePagePresenter","Running on main thread");
                } else {
                    // Install TCPdump if not already installed
                    if (RootTools.isAccessGiven()) {
                        RootTools.installBinary(context, R.raw.tcpdump,"tcpdump");
                        if (RootTools.hasBinary(context, "tcpdump")) {

                            Command command = new Command(0, "tcpdump -D") {
                                @Override
                                public void commandOutput(int id, final String line) {
                                    super.commandOutput(id, line);
                                    System.out.println(line);
                                    if (mLine != null && !mLine.isEmpty())
                                        mLine = mLine + "\n" + line;
                                    else
                                        mLine = line;
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            view.showMessage(mLine);
                                        }
                                    });
                                }
                                @Override
                                public void commandTerminated(int id, String reason) {
                                    super.commandTerminated(id, reason);
                                    System.out.println(reason);
                                }
                                @Override
                                public void commandCompleted(int id, int exitcode) {
                                    super.commandCompleted(id, exitcode);
                                    System.out.println(exitcode);
                                }
                            };
                            try {
                                RootTools.getShell(true).add(command);
                            } catch (IOException | RootDeniedException | TimeoutException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            }
        }).start();
    }

    public void StopClicked() {
        view.hideFabStop();
    }

}
