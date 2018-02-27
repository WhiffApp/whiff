package com.app.whiff.whiff.UI.PacketFileContent;

import java.util.List;

import com.app.whiff.whiff.NonRootScanner.CaptureItem;

public interface PacketFileContentPagePresenterInterface {
        List<CaptureItem> getCaptureItems(String file);
}
