package com.app.whiff.whiff.UI.PacketFile;

import com.app.whiff.whiff.NonRootScanner.FileManager;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
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
        try {
            return Arrays.asList(FileManager.listPacketFiles());
        } catch (NullPointerException e) {
            System.out.println("Directory is empty.");
        }
        return Collections.EMPTY_LIST;
    }
}
