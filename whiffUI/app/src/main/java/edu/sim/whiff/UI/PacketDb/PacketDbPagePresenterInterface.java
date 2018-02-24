package edu.sim.whiff.UI.PacketDb;

import java.util.List;
import edu.sim.whiff.Capture;


public interface PacketDbPagePresenterInterface {
    void StartClicked();
    void StopClicked();
    List<Capture> getCaptureList();
}
