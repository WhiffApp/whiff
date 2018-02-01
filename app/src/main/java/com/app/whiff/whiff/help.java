package com.app.whiff.whiff;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.stericson.RootTools.RootTools;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.*;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.util.JNetPcapFormatter;
import org.jnetpcap.util.PcapPacketArrayList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class help extends AppCompatActivity {

    String selectedDev;
    Spinner devSpinner;
    ArrayAdapter devList;
    ArrayList<String> devStringList=new ArrayList<String>();
    DBHandler dbHandler;
    String[] pcap = new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // Use root tools to check for root
        if (RootTools.isRootAvailable()) {
            System.out.println("Root Available");
        } else {
            System.out.println("Root not available.");
        }

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

        devList = new ArrayAdapter(this, android.R.layout.simple_list_item_1, devStringList);
        devSpinner = (Spinner)findViewById(R.id.devSpinner);
        devSpinner.setAdapter(devList);

        //For DB Use
        dbHandler = new DBHandler(this, null, null, 1);

        final TextView mainText = (TextView) findViewById(R.id.textView2);
        mainText.setMovementMethod(new ScrollingMovementMethod());

        Button pcapButton = (Button)findViewById(R.id.pcapButton);
        pcapButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        selectedDev = devSpinner.getSelectedItem().toString();
                        pcap[0] = "su";
                        pcap[1] = "cd /system/bin";
                        pcap[2] = "tcpdump -i " + selectedDev + " -t -c 5 ";

                        try {
                            Runtime rt = Runtime.getRuntime();
                            Process proc = rt.exec(pcap);

                            BufferedReader stdInput = new BufferedReader(new
                                    InputStreamReader(proc.getInputStream()));

                            BufferedReader stdError = new BufferedReader(new
                                    InputStreamReader(proc.getErrorStream()));

                            String temp = "";
                            while ((temp = stdInput.readLine()) != null) {
                                CapturePackets capturePackets = new CapturePackets(temp);
                                dbHandler.addPacket(capturePackets);
                            }
                            while ((temp = stdError.readLine()) != null) {
                                mainText.setText(temp);
                                break;
                            }

                        } catch (IOException e) {
                            Log.i("exception", e.toString());
                        }

                        //mainText.setText(output);

                        /*CapturePackets capturePackets = new CapturePackets(output);
                        dbHandler.dropDB();
                        dbHandler.addPacket(capturePackets);*/

                        mainText.setText(dbHandler.databaseToString());
                    }
                });

        Button dropDBButton = (Button)findViewById(R.id.dropDBButton);
        dropDBButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        dbHandler.dropDB();
                        mainText.setText("Database Cleared!");

                    }
                }
        );
    }

    /*public String callCmd(String[] command){

        String output = "";

        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(command);

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()));

            String temp, print = "";
            while ((temp = stdInput.readLine()) != null) {
                print = print + temp + "\n";
            }
            while ((temp = stdError.readLine()) != null) {
                print = print + temp;
            }

            output = print;

        } catch (IOException e) {
            Log.i("exception", e.toString());
        }

        return output;
    }*/
}


