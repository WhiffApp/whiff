package edu.sim.whiff;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.VpnService;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.os.Message;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.Selector;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.sim.whiff.Common.Utils;
import io.reactivex.subjects.BehaviorSubject;

/**
 *
 *  This class is responsible for starting and stopping the packet capture activity.
 *  It uses the VpnService provided by Android to capture the data/control packets
 *  flowing in and out of the Android device without the need of device rooting.
 *
 * @author Yeo Pei Xuan
 */

public class PacketCaptureService extends VpnService implements Handler.Callback
{
    //  The following are the commands to start/stop the PacketCaptureService
    public static final String ACTION_START = "edu.sim.whiff.PacketCaptureService.START";
    public static final String ACTION_STOP  = "edu.sim.whiff.PacketCaptureService.STOP";

    //  The following are the Packet Filtering criterias which can be specified before capture
    public static final String CAPTURE_NAME     = "edu.sim.whiff.PacketCaptureService.CAPTURE_NAME";
    public static final String PCF_PROTO_TYPE   = "edu.sim.whiff.PacketCaptureService.PCF_PROTO_TYPE";
    public static final String PCF_SRC_IP       = "edu.sim.whiff.PacketCaptureService.PCF_SRC_IP";
    public static final String PCF_SRC_PORT     = "edu.sim.whiff.PacketCaptureService.PCF_SRC_PORT";
    public static final String PCF_DST_IP       = "edu.sim.whiff.PacketCaptureService.PCF_DST_IP";
    public static final String PCF_DST_PORT     = "edu.sim.whiff.PacketCaptureService.PCF_DST_PORT";

    private static final String TAG = PacketCaptureService.class.getSimpleName();
    private static final String VPN_ADDRESS = "10.5.0.1";   // Only IPv4 support for now
    private static final String VPN_ROUTE   = "0.0.0.0";    // Intercept everything

    public static final String BROADCAST_VPN_STATE = "edu.sim.whiff.VPN_STATE";

    private ParcelFileDescriptor vpnInterface = null;

    private PendingIntent pendingIntent;

    private ConcurrentLinkedQueue<Packet> deviceToNetworkUDPQueue;
    private ConcurrentLinkedQueue<Packet> deviceToNetworkTCPQueue;
    private ConcurrentLinkedQueue<ByteBuffer> networkToDeviceQueue;
    private ExecutorService executorService;

    private Selector udpSelector;
    private Selector tcpSelector;

    private Handler mHandler;
    private static BehaviorSubject<Boolean> isRunning = BehaviorSubject.create();

    static {
        isRunning.onNext(Boolean.FALSE);    // Not running initially
    }

    public static boolean isRunning(){
        return isRunning.getValue();
    }

    public static BehaviorSubject<Boolean> getIsRunning(){
        return isRunning;
    }

    private void updateForegroundNotification(final int message) {
        startForeground(1, new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_vpn)
                .setContentText(getString(message))
                .setContentIntent(pendingIntent)
                .build());
    }

    @Override
    public boolean handleMessage(Message message) {
        Toast.makeText(this, message.what, Toast.LENGTH_SHORT).show();
        if (message.what != R.string.disconnected) {
            updateForegroundNotification(message.what);
        }
        return true;
    }

    private void setupVPN()
    {
        if (vpnInterface == null)
        {
            Builder builder = new Builder();
            builder.addAddress(VPN_ADDRESS, 32);
            builder.addRoute(VPN_ROUTE, 0);
            builder.addDnsServer("8.8.8.8");
            vpnInterface = builder.setSession(getString(R.string.app_name))
                    .setConfigureIntent(pendingIntent).establish();
        }
    }

    private void startPacketCapture(Intent intent) {

        //  Packet Capture Filter (TODO)
        String filter = null;
        filter = intent.getStringExtra(CAPTURE_NAME);
        filter = intent.getStringExtra(PCF_PROTO_TYPE);
        filter = intent.getStringExtra(PCF_SRC_IP);
        filter = intent.getStringExtra(PCF_SRC_PORT);
        filter = intent.getStringExtra(PCF_DST_IP);
        filter = intent.getStringExtra(PCF_DST_PORT);

        Boolean started = Boolean.FALSE;
        try
        {
            setupVPN();

            CaptureDAO dao = new CaptureDAO(PacketCaptureService.this.getApplicationContext());

            udpSelector = Selector.open();
            tcpSelector = Selector.open();
            deviceToNetworkUDPQueue = new ConcurrentLinkedQueue<>();
            deviceToNetworkTCPQueue = new ConcurrentLinkedQueue<>();
            networkToDeviceQueue    = new ConcurrentLinkedQueue<>();

            /*
            **  Network Activity -> TUN -> UDPOutput/TcpOutput -> tunnel  -> Remote Server
            **  Network Activity <- TUN <- UDPInput/TcpInput   <- tunnel <- Remote Server
            */
            executorService = Executors.newFixedThreadPool(5);
            executorService.submit(new UDPInput(networkToDeviceQueue, udpSelector));
            executorService.submit(new UDPOutput(deviceToNetworkUDPQueue, udpSelector, this));
            executorService.submit(new TCPInput(networkToDeviceQueue, tcpSelector));
            executorService.submit(new TCPOutput(deviceToNetworkTCPQueue, networkToDeviceQueue, tcpSelector, this));
            executorService.submit(new VPNRunnable(dao, vpnInterface.getFileDescriptor(),
                    deviceToNetworkUDPQueue, deviceToNetworkTCPQueue, networkToDeviceQueue));

            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(BROADCAST_VPN_STATE).putExtra("running", true));

            started = Boolean.TRUE;
            Log.i(TAG, "Started");
        }
        catch (IOException e)
        {
            started = Boolean.FALSE;
            // and suggest that they stop the service, since we can't do it ourselves
            Log.e(TAG, "Error starting service", e);
            cleanup();
        }

        //  Notify the application the Packet Capture process is started.
        if (started) {
            isRunning.onNext(Boolean.TRUE);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (intent == null) return START_REDELIVER_INTENT;

        String action = intent.getAction();
        if ( ACTION_START.equals(action) ) {

            startPacketCapture(intent);
            return START_STICKY;

        } else if ( ACTION_STOP.equals(intent.getAction()) ) {

            stopPacketCapture();
            return START_NOT_STICKY;

        }
        return START_REDELIVER_INTENT;
    }

    private void cleanup()
    {
        deviceToNetworkTCPQueue = null;
        deviceToNetworkUDPQueue = null;
        networkToDeviceQueue    = null;
        ByteBufferPool.clear();
        Utils.closeResources(udpSelector, tcpSelector, vpnInterface);
    }

    private void stopPacketCapture() {
        stopSelf();

        executorService.shutdownNow();
        cleanup();
        Log.i(TAG, "Stopped");

        mHandler.sendEmptyMessage(R.string.disconnected);
        stopForeground(true);

        //  Notify the application the Packet Capture process is stopped.
        isRunning.onNext(Boolean.FALSE);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        // The handler is only used to show Toast messages.
        if (mHandler == null) {
            mHandler = new Handler(this);
        }

        // Create the intent to "configure" the connection.
        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, PacketCaptureService.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onDestroy()
    {
        stopPacketCapture();
    }

    public void onRevoke() {
        stopPacketCapture();
    }

    private static class VPNRunnable implements Runnable
    {
        private static final String TAG = VPNRunnable.class.getSimpleName();

        private CaptureDAO mCaptureDAO;
        private FileDescriptor vpnFileDescriptor;

        private ConcurrentLinkedQueue<Packet> deviceToNetworkUDPQueue;
        private ConcurrentLinkedQueue<Packet> deviceToNetworkTCPQueue;
        private ConcurrentLinkedQueue<ByteBuffer> networkToDeviceQueue;

        public VPNRunnable(CaptureDAO captureDAO, FileDescriptor vpnFileDescriptor,
                           ConcurrentLinkedQueue<Packet> deviceToNetworkUDPQueue,
                           ConcurrentLinkedQueue<Packet> deviceToNetworkTCPQueue,
                           ConcurrentLinkedQueue<ByteBuffer> networkToDeviceQueue)
        {
            this.mCaptureDAO = captureDAO;
            this.vpnFileDescriptor = vpnFileDescriptor;
            this.deviceToNetworkUDPQueue = deviceToNetworkUDPQueue;
            this.deviceToNetworkTCPQueue = deviceToNetworkTCPQueue;
            this.networkToDeviceQueue = networkToDeviceQueue;
        }

        private Capture createNewCapture() {

            Capture c = new Capture();

            String filename = FileManager.generateNewFileName();
            c.name = FileManager.getFileNameWithoutExtension(filename);
            c.desc = FileManager.getFormattedTimestampFromFileName(filename);
            c.fileName = "";
            c.fileSize = 0;
            c.startTime = new Date();

            return c;
        }

        private void addPacket(Packet p, String protocol, int sourcePort, int destinationPort) {

            CaptureItem item = new CaptureItem();

            item.timestamp = new Date();
            item.sourceAddress = p.ip4Header.sourceAddress.getHostAddress();
            item.sourcePort = sourcePort;
            item.destinationAddress = p.ip4Header.destinationAddress.getHostAddress();
            item.destinationPort = destinationPort;
            item.protocol = protocol;
            item.length = p.ip4Header.totalLength;
            item.text = p.toString();
            item.data = Utils.hexdump(p.backingBuffer.array());

            mCaptureDAO.addCaptureItem(item);
        }

        @Override
        public void run()
        {
            Log.i(TAG, "Started");

            mCaptureDAO.newCapture(createNewCapture());

            /*
            **  << TCP >>
            **  Http requests -> vpnInput -> TCPOutput -> Datagram Channel
            **  Http reponses -> Datagram Channel -> TCPInput -> vpnOutput
            **
            **  << UDP >>
            **  Http requests -> vpnInput -> UDPOutput -> Datagram Channel
            **  Http reponses -> Datagram Channel -> UDPInput -> vpnOutput
            */
            FileChannel vpnInput  = new FileInputStream(vpnFileDescriptor).getChannel();
            FileChannel vpnOutput = new FileOutputStream(vpnFileDescriptor).getChannel();

            try
            {
                ByteBuffer bufferToNetwork = null;
                boolean dataSent = true;
                boolean dataReceived;
                while (!Thread.interrupted())
                {
                    if (dataSent)
                        bufferToNetwork = ByteBufferPool.acquire();
                    else
                        bufferToNetwork.clear();

                    int readBytes = vpnInput.read(bufferToNetwork);
                    if (readBytes > 0)
                    {
                        dataSent = true;

                        bufferToNetwork.flip();
                        Packet packet = new Packet(bufferToNetwork);

                        //Log.d("Outgoing", packet.toString());
                        //Log.d("VPNInput -> Outgoing", Utils.formatHexDump(bufferToNetwork.array(), 0, readBytes));

                        if (packet.isUDP())
                        {
                            addPacket(packet, Protocols.Udp,
                                    packet.udpHeader.sourcePort, packet.udpHeader.destinationPort);

                            deviceToNetworkUDPQueue.offer(packet);
                        }
                        else if (packet.isTCP())
                        {
                            addPacket(packet, Protocols.Tcp,
                                    packet.tcpHeader.sourcePort, packet.tcpHeader.destinationPort);

                            deviceToNetworkTCPQueue.offer(packet);
                        }
                        else
                        {
                            Log.w(TAG, "Unknown packet type");
                            Log.w(TAG, packet.ip4Header.toString());
                            dataSent = false;
                        }
                    }
                    else
                    {
                        dataSent = false;
                    }

                    ByteBuffer bufferFromNetwork = networkToDeviceQueue.poll();
                    if (bufferFromNetwork != null)
                    {
                        bufferFromNetwork.flip();
                        while (bufferFromNetwork.hasRemaining())
                            vpnOutput.write(bufferFromNetwork);
                        dataReceived = true;

                        ByteBufferPool.release(bufferFromNetwork);
                    }
                    else
                    {
                        dataReceived = false;
                    }

                    if (!dataSent && !dataReceived)
                        Thread.sleep(10);
                }
            }
            catch (InterruptedException e)
            {
                Log.i(TAG, "Stopping");
            }
            catch (IOException e)
            {
                Log.w(TAG, e.toString(), e);
            }
            finally
            {
                mCaptureDAO.updateCaptureEndTime(new Date());
                Utils.closeResources(vpnInput, vpnOutput, mCaptureDAO);
            }
        }
    }
}
