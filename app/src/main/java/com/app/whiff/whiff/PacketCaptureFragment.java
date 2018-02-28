package com.app.whiff.whiff;


import android.content.Intent;
import android.net.VpnService;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;

import com.app.whiff.whiff.NonRootScanner.UI.NonRootScanner;
import com.app.whiff.whiff.NonRootScanner.Utils;
import com.app.whiff.whiff.RootScanner.TCPdump;
import com.app.whiff.whiff.RootScanner.TCPdumpService;
import com.app.whiff.whiff.RootScanner.UI.RootScanner;
import com.app.whiff.whiff.UI.PacketFile.PacketFilePage;
import com.stericson.RootTools.RootTools;

import org.jnetpcap.PcapService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import jp.co.taosoftware.android.packetcapture.PacketCaptureService;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class PacketCaptureFragment extends Fragment {

    // Buttons to launch other activities
    public Button DisplayButton;
    public Switch NonRootSwitch;
    public Switch RootSwitch;
    public Switch ARPSpooferSwitch;
    public Spinner devSpinner;
    public ArrayAdapter devList;
    public ArrayList<String> devStringList=new ArrayList<String>();

    public TCPdump tcpdump;
    public TCPdumpService tcpDumpService;

    public PacketCaptureFragment() {
        // Required empty public constructor
    }

    private static final int VPN_REQUEST_CODE = 0x0F;

    private boolean waitingForVPNStart;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View psView = inflater.inflate(R.layout.fragment_packet_capture, container, false);

        // Create UI objects
        devSpinner = psView.findViewById(R.id.devSpinner);
        DisplayButton = (Button) psView.findViewById(R.id.DisplayButton);
        RootSwitch = (Switch) psView.findViewById(R.id.switch1);
        NonRootSwitch = (Switch) psView.findViewById(R.id.switch2);
        ARPSpooferSwitch = (Switch) psView.findViewById(R.id.switch3);

        new findNetworkInterfaces().execute();

        
        DisplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        tcpdump = new TCPdump(getActivity().getApplicationContext());
        tcpdump.installTCPdump();

        if(!(RootTools.isAccessGiven())){
            RootSwitch.setEnabled(false);
            ARPSpooferSwitch.setEnabled(false);
        } else{
            RootSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (RootSwitch.isChecked()) {
                        NonRootSwitch.setEnabled(false);
                        ARPSpooferSwitch.setEnabled(true);

                        // Start Root Sniffer Activity
                        String TCPdumpParams = "-U -w whiff.pcap";
                        startTCPdumpService(TCPdumpParams);
                    } else if (!RootSwitch.isChecked()) {
                        stopTCPdumpService();
                    }

                        //Replace with sniffing function
                  /*selectedDev = devSpinner.getSelectedItem().toString();
                    pcap[0] = "su";
                    pcap[1] = "cd /system/bin";
                    pcap[2] = "tcpdump -i " + selectedDev + " -c 5 -w /sdcard/whiff.pcap";

                    try {
                        Runtime rt = Runtime.getRuntime();
                        Process proc = rt.exec(pcap);
                    } catch (IOException e) {
                        Log.i("exception", e.toString());
                    }*/
                    else if (ARPSpooferSwitch.isChecked()){
                        NonRootSwitch.setEnabled(false);
                    }
                    else{
                        NonRootSwitch.setEnabled(true);
                        ARPSpooferSwitch.setEnabled(true);
                    }
                }
            });

            NonRootSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NonRootSwitch.isChecked()){
                        RootSwitch.setEnabled(false);
                        ARPSpooferSwitch.setEnabled(false);

                        // Start non-root sniffer service
                        startVPN();

                    }
                    else if(RootTools.isRootAvailable()){
                        RootSwitch.setEnabled(true);
                        ARPSpooferSwitch.setEnabled(true);
                    }
                    else{
                        RootSwitch.setEnabled(false);
                        ARPSpooferSwitch.setEnabled(false);
                    }
                }
            });

            ARPSpooferSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ARPSpooferSwitch.isChecked()){
                        NonRootSwitch.setEnabled(false);
                    }
                    else if (RootSwitch.isChecked()){
                        NonRootSwitch.setEnabled(false);
                    }
                    else{
                        NonRootSwitch.setEnabled(true);
                    }
                }
            });


        }




        return psView;
    }












    private void startVPN()
    {
        Intent vpnIntent = VpnService.prepare(getActivity());
        if (vpnIntent != null)
            startActivityForResult(vpnIntent, VPN_REQUEST_CODE);
        else
            onActivityResult(VPN_REQUEST_CODE, RESULT_OK, null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VPN_REQUEST_CODE && resultCode == RESULT_OK)
        {
            waitingForVPNStart = true;
            Intent i = new Intent(getActivity(), PacketCaptureService.class);
            i.putExtra("CaptureName", Utils.getUniqueTimestampName());
            getActivity().startService(i);
            // enableButton(false);
        }
    }

    private void startOrStopCapture()
    {
        Intent vpnIntent = VpnService.prepare(getActivity());
        if (vpnIntent != null)
            startActivityForResult(vpnIntent, VPN_REQUEST_CODE);
        else
            onActivityResult(VPN_REQUEST_CODE, RESULT_OK, null);
    }

    private void stopCapture() {

        boolean isRunning = PcapService.isRunning();
        if (isRunning) {
            Intent i = getServiceIntent(PcapService.ACTION_STOP);
            getActivity().startService(i);
        }
    }

    private Intent getServiceIntent(String action) {
        Intent i = new Intent(getActivity(), PcapService.class);
        i.setAction(action);

        return i;
    }

    public void startTCPdumpService(String params) {
        Intent intent = new Intent(getActivity(), TCPdumpService.class);
        intent.putExtra(TCPdumpService.ACTION_START,params);
        getActivity().startService(intent);
    }
    public void stopTCPdumpService() {
        new TCPdump(getActivity()).stopSniff();
        Intent intent = new Intent(getActivity(), TCPdumpService.class);
        getActivity().stopService(intent);
    }

    private class findNetworkInterfaces extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if(RootTools.isRootAvailable()) {
                final String[] listDev = new String[3];
                listDev[0] = "su";
                listDev[1] = "cd /system/bin";
                listDev[2] = "tcpdump -D";
                try {
                    Runtime rt = Runtime.getRuntime();
                    Process proc = rt.exec(listDev);

                    BufferedReader stdInput = new BufferedReader(new
                            InputStreamReader(proc.getInputStream()));

                    String temp;
                    while ((temp = stdInput.readLine()) != null) {
                        int dotPos = temp.indexOf(".");
                        temp = temp.substring(dotPos + 1, temp.length());
                        int spacePos = temp.indexOf(" ");
                        if (spacePos > 0) {
                            temp = temp.substring(0, spacePos);
                        }
                        devStringList.add(temp);
                    }
                } catch (IOException e) {
                    Log.i("exception", e.toString());
                }
            }
            else {
                devStringList.add("Root Unavailable");
                devSpinner.setEnabled(false);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            devList = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, devStringList);
            devSpinner.setAdapter(devList);
        }
    }


}
