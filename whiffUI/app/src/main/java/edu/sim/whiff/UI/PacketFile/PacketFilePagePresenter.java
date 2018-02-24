package edu.sim.whiff.UI.PacketFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import edu.sim.whiff.FileManager;


public class PacketFilePagePresenter implements PacketFilePagePresenterInterface {

    public PacketFilePageViewInterface view;

    public PacketFilePagePresenter(PacketFilePage page) {
        view = page;
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
