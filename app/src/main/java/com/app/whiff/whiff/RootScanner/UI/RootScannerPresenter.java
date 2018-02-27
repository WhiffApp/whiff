package com.app.whiff.whiff.RootScanner.UI;

import com.app.whiff.whiff.NonRootScanner.FileManager;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    public List<File> listPacketFiles() {
        try {
            return Arrays.asList(FileManager.listPacketFiles());
        } catch (NullPointerException e) {
            System.out.println("Directory is empty.");
        }
        return Collections.EMPTY_LIST;
    }
}

