package com.app.whiff.whiff.UI.PacketFile;

import com.app.whiff.whiff.NonRootScanner.FileManager;

import java.io.File;
import java.util.Arrays;
import java.util.List;


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
