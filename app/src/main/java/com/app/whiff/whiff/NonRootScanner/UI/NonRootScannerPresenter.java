package com.app.whiff.whiff.NonRootScanner.UI;

<<<<<<< HEAD
/**
 * Created by gyych on 1/1/2018.
 */

public class NonRootScannerPresenter implements NonRootScannerPresenterInterface {
    public NonRootScannerViewInterface view;
    public NonRootScannerPresenter(NonRootScanner homepage)
    {
        view = homepage;
    }
    public void StartClicked(){
        view.hideFabStart();
    }
    public void StopClicked(){
        view.hideFabStop();
    }
=======
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

>>>>>>> Thursday-Afternoon-Brunch
}
