package edu.sim.whiff.UI.PacketDbContent;

import android.util.Log;
import org.jnetpcap.Pcap;
import org.jnetpcap.nio.JMemory;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.JProtocol;
import org.jnetpcap.protocol.network.Arp;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.network.Ip6;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.sim.whiff.CaptureDAO;
import edu.sim.whiff.CaptureItem;
import edu.sim.whiff.Protocols;


public class PacketDbContentPresenter implements PacketDbContentPagePresenterInterface {

    public PacketDbContentPageViewInterface view;
    private CaptureDAO mDataAccess;

    public PacketDbContentPresenter(PacketDbContentPageViewInterface page, CaptureDAO dataAccess)
    {
        view = page;
        mDataAccess = dataAccess;
    }

    public List<CaptureItem> getCaptureItems(long captureID) {

        return mDataAccess.getCaptureItems(captureID);
    }
}
