package com.app.whiff.whiff.UI.PacketFile;

import java.io.File;
import java.util.List;


public interface PacketFilePagePresenterInterface {
        void StartClicked();
        void StopClicked();
        List<File> listPacketFiles();
}