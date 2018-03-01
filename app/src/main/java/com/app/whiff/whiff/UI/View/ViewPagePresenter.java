package com.app.whiff.whiff.UI.View;


import com.app.whiff.whiff.NonRootScanner.Capture;
import com.app.whiff.whiff.NonRootScanner.CaptureDAO;
import com.app.whiff.whiff.NonRootScanner.PacketContentFilter;

import java.util.List;

public class ViewPagePresenter implements ViewPagePresenterInterface {

    public ViewPageViewInterface view;
    private CaptureDAO mDataAccess;
    private PacketContentFilter mFilter = new PacketContentFilter();

    public ViewPagePresenter(ViewPage page, CaptureDAO dataAccess)
    {
        view = page;
        mDataAccess = dataAccess;
    }

    public List<Capture> getCaptureList() {
        return mDataAccess.getAllCapture();
    }

    public PacketContentFilter getPacketContentFilter() {
        return mFilter;
    }
}
