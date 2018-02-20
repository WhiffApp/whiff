package edu.sim.whiff.UI.HomePage;

import java.io.File;
import java.util.List;

/**
 * Created by gyych on 1/1/2018.
 */

    public interface HomePagePresenterInterface {
        void StartClicked();
        void StopClicked();
        List<File> listPacketFiles();
}
