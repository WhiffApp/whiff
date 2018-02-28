package com.app.whiff.whiff.UI.PacketDb;


import java.util.List;

import com.app.whiff.whiff.NonRootScanner.Capture;
import com.app.whiff.whiff.NonRootScanner.CaptureDAO;

public class PacketDbPagePresenter implements PacketDbPagePresenterInterface {

    public PacketDbPageViewInterface view;
    private CaptureDAO mDataAccess;

    public PacketDbPagePresenter(PacketDbPage page, CaptureDAO dataAccess)
    {
        view = page;
        mDataAccess = dataAccess;
    }

    public void StartClicked(){
        view.hideFabStart();
    }

    public void StopClicked(){
        view.hideFabStop();
    }

    public List<Capture> getCaptureList() {
        return mDataAccess.getAllCapture();
    }
}
