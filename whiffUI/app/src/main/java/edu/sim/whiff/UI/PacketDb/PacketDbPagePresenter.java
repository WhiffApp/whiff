package edu.sim.whiff.UI.PacketDb;


import java.util.List;

import edu.sim.whiff.Capture;
import edu.sim.whiff.CaptureDAO;

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
