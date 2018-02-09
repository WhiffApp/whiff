package com.app.whiff.whiff.RootScanner;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TCPdumpService extends Service {
    public TCPdumpService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
        // throw new UnsupportedOperationException("Not yet implemented");
    }
}
