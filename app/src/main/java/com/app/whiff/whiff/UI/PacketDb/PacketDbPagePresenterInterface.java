package com.app.whiff.whiff.UI.PacketDb;

import java.util.List;

import com.app.whiff.whiff.NonRootScanner.Capture;
import com.app.whiff.whiff.NonRootScanner.PacketContentFilter;


public interface PacketDbPagePresenterInterface {
    void StartClicked();
    void StopClicked();
    List<Capture> getCaptureList();
    PacketContentFilter getPacketContentFilter();
}
