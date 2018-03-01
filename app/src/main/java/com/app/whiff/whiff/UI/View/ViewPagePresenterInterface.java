package com.app.whiff.whiff.UI.View;

import com.app.whiff.whiff.NonRootScanner.Capture;
import com.app.whiff.whiff.NonRootScanner.PacketContentFilter;

import java.util.List;


public interface ViewPagePresenterInterface {
    List<Capture> getCaptureList();
    PacketContentFilter getPacketContentFilter();
}
