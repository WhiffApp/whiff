package com.app.whiff.whiff.UI.PacketDbContent;

import java.util.List;

import com.app.whiff.whiff.NonRootScanner.CaptureItem;

public interface PacketDbContentPagePresenterInterface {
        List<CaptureItem> getCaptureItems(long captureID);
}
