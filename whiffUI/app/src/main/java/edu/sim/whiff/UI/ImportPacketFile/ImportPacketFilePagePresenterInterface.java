package edu.sim.whiff.UI.ImportPacketFile;

import java.io.File;
import java.util.List;


public interface ImportPacketFilePagePresenterInterface {
        List<File> listPacketFiles();
        long importPacketFile(File pcapFile);
}