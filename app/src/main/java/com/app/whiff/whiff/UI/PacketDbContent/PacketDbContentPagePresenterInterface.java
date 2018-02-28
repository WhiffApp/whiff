package com.app.whiff.whiff.UI.PacketDbContent;

import java.util.List;

import com.app.whiff.whiff.NonRootScanner.CaptureItem;
import com.app.whiff.whiff.NonRootScanner.PacketContentFilterQuery;

public interface PacketDbContentPagePresenterInterface {
        List<CaptureItem> getCaptureItems(long captureID);
        List<CaptureItem> getCaptureItems(PacketContentFilterQuery query);
}
