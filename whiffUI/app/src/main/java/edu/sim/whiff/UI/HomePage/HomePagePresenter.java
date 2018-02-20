package edu.sim.whiff.UI.HomePage;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import edu.sim.whiff.FileManager;

/**
 * Created by gyych on 1/1/2018.
 */

public class HomePagePresenter implements HomePagePresenterInterface {
    public HomePageViewInterface view;
    public HomePagePresenter(HomePage homepage)
    {
        view = homepage;
    }
    public void StartClicked(){
        view.hideFabStart();
    }
    public void StopClicked(){
        view.hideFabStop();
    }

    public List<File> listPacketFiles() {
        return Arrays.asList(FileManager.listPacketFiles());
    }
}
