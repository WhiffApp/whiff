package edu.sim.whiff.UI.PacketDbContent;

import java.util.List;

import edu.sim.whiff.CaptureItem;

public interface PacketDbContentPagePresenterInterface {
        List<CaptureItem> getCaptureItems(long captureID);
}
