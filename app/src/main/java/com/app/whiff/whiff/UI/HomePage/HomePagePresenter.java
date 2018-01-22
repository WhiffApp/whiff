package com.app.whiff.whiff.UI.HomePage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.app.whiff.whiff.TCPdump;


/**
 * HomePagePresenter
 * Actions to take when buttons are clicked in HomePage activity.
 */

public class HomePagePresenter implements HomePagePresenterInterface {

    public Context context;
    public HomePageViewInterface view;
    public String mLine = "";      // Message from handler
    public TCPdump tcpdump;

    // Handler that handles messages sent from TCPdump thread
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

            // The following code is unnecessary because HomePagePresenter is running
            // on the UI thread.
//            homePageHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    view.showMessage(mLine);
//                }
//            });

        }
    };

    public HomePagePresenter(HomePage homepage) {
        view = homepage;
        context = homepage;
        tcpdump = new TCPdump(homepage, TCPdumpHandler);
    }

    public void StartClicked() {
        view.hideFabStart();
        tcpdump.installTCPdump();   // Install TCPdump
        tcpdump.doSniff();  // Begin packet capture
    }

    public void StopClicked() {
        view.hideFabStop();
        tcpdump.stopSniff();
    }

}
