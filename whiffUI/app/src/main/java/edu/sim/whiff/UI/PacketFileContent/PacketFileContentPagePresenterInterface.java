package edu.sim.whiff.UI.PacketFileContent;

import java.util.List;

import edu.sim.whiff.CaptureItem;

public interface PacketFileContentPagePresenterInterface {
        List<CaptureItem> getCaptureItems(String file);
}
