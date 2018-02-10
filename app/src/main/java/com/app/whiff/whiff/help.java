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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class help extends AppCompatActivity {

    String selectedDev;
    Spinner devSpinner;
    ArrayAdapter devList;
    ArrayList<String> devStringList=new ArrayList<String>();
    DBHandler dbHandler;
    String[] pcap = new String[3];
    String[] readPcap = new String[3];

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
                        pcap[2] = "tcpdump -i " + selectedDev + " -c 5 -w /sdcard/whiff.pcap";

                        try {
                            Runtime rt = Runtime.getRuntime();
                            Process proc = rt.exec(pcap);
                        } catch (IOException e) {
                            Log.i("exception", e.toString());
                        }

                        readPcap[0] = "su";
                        readPcap[1] = "cd /system/bin";
                        readPcap[2] = "tcpdump -r sdcard/whiff.pcap -nttttvv";

                        try {
                            Runtime rt = Runtime.getRuntime();
                            Process proc = rt.exec(readPcap);

                            BufferedReader stdInput = new BufferedReader(new
                                    InputStreamReader(proc.getInputStream()));

                            BufferedReader stdError = new BufferedReader(new
                                    InputStreamReader(proc.getErrorStream()));

                            String temp, stringToDB = "";
                            int count = 1;
                            while ((temp = stdInput.readLine()) != null) {
                                boolean isNewRow = newPacket(temp);
                                if(isNewRow == true){
                                    if(count == 1){
                                        stringToDB = temp;
                                        count++;
                                    }
                                    else{
                                        parsePacket(stringToDB);
                                        stringToDB = temp;
                                        count++;
                                    }
                                }
                                else{
                                    stringToDB += temp;
                                }

                            }

                            if((stdInput.readLine()) == null){
                                parsePacket(stringToDB);
                                count++;
                            }

                            while ((temp = stdError.readLine()) != null) {
                                mainText.setText(temp);
                                break;
                            }

                        } catch (IOException e) {
                            Log.i("exception", e.toString());
                        }

                        mainText.setText(dbHandler.databaseToString());
                    }
                });

        Button dropDBButton = (Button)findViewById(R.id.dropDBButton);
        dropDBButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        dbHandler.dropDB();
                        mainText.setText("Database Cleared!");
                        mainText.scrollTo(0,0);
                        try {
                            Runtime.getRuntime().exec("rm /sdcard/whiff.pcap");
                        } catch (IOException e) {
                            Log.i("exception", e.toString());
                        }
                    }
                }
        );
    }

    public void parsePacket(String stringToDB){

        String[] ipAdd = getIP(stringToDB);
        CapturePackets capturePackets = new CapturePackets(ipAdd[0],ipAdd[1],stringToDB);
        dbHandler.addPacket(capturePackets);
    }

    public String[] getIP(String stringToDB){
        String[] ipAdd = new String[2];
        Pattern ipPattern = Pattern.compile("(\\d{1,3})(\\.)(\\d{1,3})(\\.)(\\d{1,3})(\\.)(\\d{1,3}).*");
        String[] word = stringToDB.split(" ");
        int count = 0;


        for(int i=0;i<word.length;i++){
            Matcher ipMatcher = ipPattern.matcher(word[i]);
            if (ipMatcher.matches() == true){
                ipAdd[count] = word[i];
                count++;
                if(count > 1){
                    break;
                }
            }
        }

        for(int i=0;i<2;i++){
            ipAdd[i] = convertToIP(ipAdd[i]);
        }

        return ipAdd;
    }

    public String convertToIP(String stringToConvert){
        if (stringToConvert !=null) {
            String[] temp = stringToConvert.split(Pattern.quote("."));
            stringToConvert = temp[0] + "." + temp[1] + "." + temp[2] + "." + temp[3];
        }
        return stringToConvert;
    }

    //Check if line is start of new packet
    public boolean newPacket(String line){
        String temp1 = line.split(" ")[0];
        String temp2 = line.split(" ")[1];

        Pattern p1 = Pattern.compile("^(\\d{4})(-)(\\d{2})(-)(\\d{2})");
        Pattern p2 = Pattern.compile("(\\d{2})(:)(\\d{2})(:)(\\d{2})(.)(\\d+)");

        Matcher m1 = p1.matcher(temp1);
        Matcher m2 = p2.matcher(temp2);

        boolean b1 = m1.matches();
        boolean b2 = m2.matches();

        if(b1 == true && b2 == true){
            return true;
        }
        else{
            return false;
        }
    }
}


