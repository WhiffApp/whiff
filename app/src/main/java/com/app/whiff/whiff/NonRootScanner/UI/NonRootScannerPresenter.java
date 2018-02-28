package com.app.whiff.whiff.NonRootScanner.UI;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.app.whiff.whiff.NonRootScanner.FileManager;
import com.app.whiff.whiff.NonRootScanner.PacketContentFilter;

/**
 *
 * This class manages the state between UI and Data Model for Packet Content Filtering
 *
 * @author Yeo Pei Xuan
 */

public class NonRootScannerPresenter implements NonRootScannerPresenterInterface {

    public NonRootScannerViewInterface view;
    private PacketContentFilter mFilter;

    public NonRootScannerPresenter(NonRootScanner homepage)
    {
        view = homepage;
        mFilter = new PacketContentFilter();
    }

    public PacketContentFilter getPacketContentFilter() {
        return mFilter;
    }

}
