package com.app.whiff.whiff.RootScanner.UI;

import java.io.File;
import java.util.List;

/**
 * Created by gyych on 1/1/2018.
 */

    public interface RootScannerPresenterInterface {
    void ActivityStarted();
    void StartClicked();
    void StopClicked();
    List<File> listPacketFiles();

}
