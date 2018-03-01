package com.app.whiff.whiff.RootScanner.UI;

<<<<<<< HEAD
=======
import com.app.whiff.whiff.NonRootScanner.FileManager;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

>>>>>>> Thursday-Afternoon-Brunch
/**
 * RootScannerPresenter
 * Actions to take when buttons are clicked in RootScanner activity.
 */

public class RootScannerPresenter implements RootScannerPresenterInterface {

    public RootScannerViewInterface view;

    public RootScannerPresenter(RootScanner homepage) {
        view = homepage;
    }

    public void ActivityStarted() {
        view.installTCPdump();
    }

    public void StartClicked() {
        view.hideFabStart();
    }

    public void StopClicked() {
        view.hideFabStop();
    }

<<<<<<< HEAD
}
=======
    public List<File> listPacketFiles() {
        return Arrays.asList(FileManager.listPacketFiles());
    }
}

>>>>>>> Thursday-Afternoon-Brunch
