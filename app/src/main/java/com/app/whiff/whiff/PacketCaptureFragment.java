package com.app.whiff.whiff;


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

import com.stericson.RootTools.RootTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


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

    public PacketCaptureFragment() {
        // Required empty public constructor
    }


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


        //Array Adapter for spinner
        ArrayAdapter devList;
        ArrayList<String> devStringList=new ArrayList<String>();
        // Create new Thread for
        Thread t1 = new Thread() {

            @Override
            public void run() {
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
                else{
                    devStringList.add("Root Unavailable");
                    devSpinner.setEnabled(false);
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        };
        t1.start();
        devList = new ArrayAdapter(psView.getContext(), android.R.layout.simple_list_item_1, devStringList);
        devSpinner.setAdapter(devList);

        DisplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        RootSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RootSwitch.isChecked()){
                    NonRootSwitch.setEnabled(false);
                    ARPSpooferSwitch.setEnabled(false);

                    //Replace with sniffing function
                  /*  selectedDev = devSpinner.getSelectedItem().toString();
                    pcap[0] = "su";
                    pcap[1] = "cd /system/bin";
                    pcap[2] = "tcpdump -i " + selectedDev + " -c 5 -w /sdcard/whiff.pcap";

                    try {
                        Runtime rt = Runtime.getRuntime();
                        Process proc = rt.exec(pcap);
                    } catch (IOException e) {
                        Log.i("exception", e.toString());
                    }*/
                }
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

        if(!(RootTools.isRootAvailable())){
            RootSwitch.setEnabled(false);
            ARPSpooferSwitch.setEnabled(false);
        }


        return psView;
    }

}
