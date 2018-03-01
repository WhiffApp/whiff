package com.app.whiff.whiff.UI.ImportPacketFile;

import android.database.sqlite.SQLiteException;
import android.util.Log;

import org.jnetpcap.Pcap;
import org.jnetpcap.nio.JMemory;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Arp;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.network.Ip6;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.app.whiff.whiff.NonRootScanner.Capture;
import com.app.whiff.whiff.NonRootScanner.CaptureDAO;
import com.app.whiff.whiff.NonRootScanner.CaptureItem;
import com.app.whiff.whiff.NonRootScanner.FileManager;
import com.app.whiff.whiff.NonRootScanner.Packet;
import com.app.whiff.whiff.NonRootScanner.Protocols;

/**
 *
 * This class imports the .pcap file contents into the database
 *
 * @author Yeo Pei Xuan
 */

public class ImportPacketFilePagePresenter implements ImportPacketFilePagePresenterInterface {

    public ImportPacketFilePageViewInterface view;
    private CaptureDAO mDataAccess;
    private final String TAG = ImportPacketFilePagePresenter.class.getSimpleName();

    public ImportPacketFilePagePresenter(ImportPacketFilePage page, CaptureDAO dataAccess) {
        view = page;
        mDataAccess = dataAccess;
    }

    public List<File> listPacketFiles() {
        return Arrays.asList(FileManager.listPacketFiles());
    }

    public long importPacketFile(File file) {

        long rowsCount = 0L;

        try {

            Capture newImport = createNewCapture(file);
            if (!mDataAccess.newCapture(newImport)) {
                return 0;
            }

            long captureID = newImport.ID;

            Pcap pcap = Pcap.openOffline(file.getAbsolutePath(), new StringBuilder());

            PcapPacket detailsPacket = new PcapPacket(JMemory.POINTER);
            Tcp tcp = new Tcp();
            Udp udp = new Udp();
            Arp arp = new Arp();

            while (pcap.nextEx(detailsPacket) == Pcap.NEXT_EX_OK) {
                //type
                CaptureItem item = new CaptureItem();
                item.captureID = captureID;

                if (detailsPacket.hasHeader(tcp)) {
                    tcp = detailsPacket.getHeader(tcp);
                    item.protocol = Protocols.Tcp;
                    item.sourcePort = tcp.source();
                    item.destinationPort = tcp.destination();

                } else if (detailsPacket.hasHeader(udp)) {
                    udp = detailsPacket.getHeader(udp);
                    item.protocol = Protocols.Udp;
                    item.sourcePort = udp.source();
                    item.destinationPort = udp.destination();
                    //Log.d("UDP", tcp.toHexdump());

                } else if (detailsPacket.hasHeader(arp)) {
                    item.protocol = Protocols.Arp;
                } else {
                    item.protocol = Protocols.Unknown;
                }
                //address
                String sip = null, dip = null;
                Ip4 ip = new Ip4();
                Ip6 ip6 = new Ip6();
                if (detailsPacket.hasHeader(ip)) {
                    sip = FormatUtils.ip(ip.source());
                    dip = FormatUtils.ip(ip.destination());

                } else if (detailsPacket.hasHeader(ip6)) {

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
                    item.text = getPacketDetailsText(detailsPacket);

                } catch (Exception e) {
                }

                try {
                    item.data = detailsPacket.toHexdump(item.length, false, false, true);

                } catch (Exception e) {
                }

                mDataAccess.addCaptureItem(item);
                rowsCount = rowsCount + 1;
            }

            mDataAccess.updateCaptureEndTime(newImport.endTime);

        } catch(SQLiteException e) {
            Log.e(TAG, e.getMessage());
        }

        return rowsCount;
    }

    private Capture createNewCapture(File f) {

        Capture c = new Capture();

        String filename = f.getName();
        c.name = FileManager.getFileNameWithoutExtension(filename);
        c.desc = FileManager.getFormattedTimestampFromFileName(filename);
        c.fileName = filename;
        c.fileSize = f.length();
        c.startTime = new Date();
        c.endTime = new Date(f.lastModified());

        return c;
    }

    private String getPacketDetailsText(PcapPacket packet) {

        final StringBuilder sb = new StringBuilder();

        sb.append("Packet { ");
        buildIPHeader(sb, packet);

        Tcp tcp = new Tcp();
        Udp udp = new Udp();

        if (packet.hasHeader(tcp)) {
            tcp = packet.getHeader(tcp);
            buildTcpHeader(sb, tcp);

        } else if (packet.hasHeader(udp)) {
            udp = packet.getHeader(udp);
            buildUdpHeader(sb, udp);
        }
        sb.append(", Payload Size = ").append(packet.getTotalSize());
        sb.append(" }");

        return sb.toString();
    }

    private void buildIPHeader(StringBuilder sb, PcapPacket packet) {

        Ip4 ip4 = new Ip4();
        Ip6 ip6 = new Ip6();

        if (packet.hasHeader(ip4)) {
            buildIPv4Header(sb, ip4);

        } else if (packet.hasHeader(ip6)) {
            buildIPv6Header(sb, ip6);
        }
    }

    private void buildIPv4Header(StringBuilder sb, Ip4 ip4) {

        sb.append("IP Header [ ");
        sb.append("Version = ").append(ip4.version());
        sb.append(", Total Length = ").append(ip4.length());
        sb.append(", Source IP = ").append(FormatUtils.ip(ip4.source()));
        sb.append(", Destination IP = ").append(FormatUtils.ip(ip4.destination()));
        sb.append(" ], ");
    }

    private void buildIPv6Header(StringBuilder sb, Ip6 ip6) {

        sb.append("IP Header [ ");
        sb.append("Version = ").append(ip6.version());
        sb.append(", Total Length = ").append(ip6.length());
        sb.append(", Source IP = ").append(FormatUtils.ip(ip6.source()));
        sb.append(", Destination IP = ").append(FormatUtils.ip(ip6.destination()));
        sb.append(" ], ");
    }

    private void buildTcpHeader(StringBuilder sb, Tcp tcp) {

        sb.append("TCP Header [ ");
        sb.append("Source Port = ").append(tcp.source());
        sb.append(", Destination Port = ").append(tcp.destination());
        sb.append(", Sequence Number = ").append(tcp.seq());
        sb.append(", Acknowledgement Number = ").append(tcp.ack());
        sb.append(", Flags =");
        if (tcp.flags_FIN()) sb.append(" FIN");
        if (tcp.flags_SYN()) sb.append(" SYN");
        if (tcp.flags_RST()) sb.append(" RST");
        if (tcp.flags_PSH()) sb.append(" PSH");
        if (tcp.flags_ACK()) sb.append(" ACK");
        if (tcp.flags_URG()) sb.append(" URG");
        sb.append(", Window Size = ").append(tcp.window());
        sb.append(", Checksum = ").append(tcp.checksum());
        sb.append(" ]");
    }

    private void buildUdpHeader(StringBuilder sb, Udp udp) {

        sb.append("UDP Header [ ");
        sb.append("Source Port = ").append(udp.source());
        sb.append(", Destination Port = ").append(udp.destination());
        sb.append(", Length = ").append(udp.length());
        sb.append(", Checksum = ").append(udp.checksum());
        sb.append(" ]");
    }
}
