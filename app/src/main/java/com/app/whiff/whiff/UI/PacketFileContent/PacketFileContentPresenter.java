package com.app.whiff.whiff.UI.PacketFileContent;

import org.jnetpcap.Pcap;
import org.jnetpcap.nio.JMemory;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Arp;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.network.Ip6;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.app.whiff.whiff.NonRootScanner.CaptureItem;
import com.app.whiff.whiff.NonRootScanner.Protocols;

public class PacketFileContentPresenter implements PacketFileContentPagePresenterInterface {
    public PacketFileContentPageViewInterface view;

    public PacketFileContentPresenter(PacketFileContentPageViewInterface page)
    {
        view = page;
    }

    public List<CaptureItem> getCaptureItems(String file) {

        ArrayList<CaptureItem> items = new ArrayList<>(100);

        Pcap pcap = Pcap.openOffline(file, new StringBuilder());
        PcapPacket detailsPacket = new PcapPacket(JMemory.POINTER);
        Tcp tcp = new Tcp();
        Udp udp = new Udp();
        Arp arp = new Arp();
        while ( pcap.nextEx(detailsPacket) == Pcap.NEXT_EX_OK ) {
            //type
            CaptureItem item = new CaptureItem();
            if (detailsPacket.hasHeader(tcp)) {
                tcp = detailsPacket.getHeader(tcp);
                item.protocol = Protocols.Tcp;
                item.sourcePort = tcp.source();
                item.destinationPort = tcp.destination();
                //Log.d("TCP", tcp.toHexdump());

            } else if(detailsPacket.hasHeader(udp)) {
                udp = detailsPacket.getHeader(udp);
                item.protocol = Protocols.Udp;
                item.sourcePort = udp.source();
                item.destinationPort = udp.destination();
                //Log.d("UDP", tcp.toHexdump());

            } else if(detailsPacket.hasHeader(arp)) {
                item.protocol = Protocols.Arp;
            } else {
                item.protocol = Protocols.Unknown;
            }
            //address
            String sip=null, dip=null;
            Ip4 ip = new Ip4();
            Ip6 ip6 = new Ip6();
            if(detailsPacket.hasHeader(ip)) {
                sip = FormatUtils.ip(ip.source());
                dip = FormatUtils.ip(ip.destination());
            } else if(detailsPacket.hasHeader(ip6)) {
                sip = FormatUtils.ip(ip6.source());
                dip = FormatUtils.ip(ip6.destination());
            }
            item.sourceAddress = sip;
            item.destinationAddress = dip;

            //length
            item.length = detailsPacket.getTotalSize();

            //time
            long timestampInMillis = detailsPacket.getCaptureHeader().timestampInMillis();
            item.timestamp = new Date(timestampInMillis);

            try {
                item.text = detailsPacket.toHexdump(item.length, false, true, false);

            } catch(Exception e) {
            }

            try {
                item.data = detailsPacket.toHexdump(item.length, false, false, true);

            } catch(Exception e) {
            }

            items.add(item);
        }

        return items;
    }
}
