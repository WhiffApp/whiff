package com.app.whiff.whiff;

import android.content.Context;
import android.content.ContextWrapper;

import com.stericson.RootTools.RootTools;

/**
 * Created by danie on 19/1/2018.
 */


public class TCPdump extends ContextWrapper {

    public static final String TCPdumpBinaryPath = "/data/data/com.app.whiff.whiff/files/tcpdump";

    public TCPdump(Context base) {
        super(base);
    }

    public void installTCPdump() {
    }
}
