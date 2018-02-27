package com.app.whiff.whiff.UI.PacketDbContent;

import java.util.List;

import com.app.whiff.whiff.NonRootScanner.CaptureDAO;
import com.app.whiff.whiff.NonRootScanner.CaptureItem;


public class PacketDbContentPresenter implements PacketDbContentPagePresenterInterface {

    public PacketDbContentPageViewInterface view;
    private CaptureDAO mDataAccess;

    public PacketDbContentPresenter(PacketDbContentPageViewInterface page, CaptureDAO dataAccess)
    {
        view = page;
        mDataAccess = dataAccess;
    }

    public List<CaptureItem> getCaptureItems(long captureID) {

        return mDataAccess.getCaptureItems(captureID);
    }
}
