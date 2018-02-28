package com.app.whiff.whiff.NonRootScanner.UI;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.app.whiff.whiff.NonRootScanner.FileManager;

/**
 * Created by gyych on 1/1/2018.
 */

public class NonRootScannerPresenter implements NonRootScannerPresenterInterface {
    public NonRootScannerViewInterface view;
    public NonRootScannerPresenter(NonRootScanner homepage)
    {
        view = homepage;
    }
    public void StopClicked() {}
    public void StartClicked() {}

    public List<File> listPacketFiles() {return Arrays.asList(FileManager.listPacketFiles()); }
}
