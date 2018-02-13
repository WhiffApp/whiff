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
import org.jnetpcap.protocol.network.Arp;
import org.jnetpcap.util.JNetPcapFormatter;
import org.jnetpcap.util.PcapPacketArrayList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Source;

public class help extends AppCompatActivity {

    String selectedDev;
    Spinner devSpinner;
    ArrayAdapter devList;
    ArrayList<String> devStringList=new ArrayList<String>();
    DBHandler dbHandler;
    String[] pcap = new String[3];
    String[] readPcap = new String[3];
    String[] getPcapLines = new String[3];
    int TotalLines;

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

                        /*getPcapLines[0] = "su";
                        getPcapLines[1] = "cd /system/bin";
                        getPcapLines[2] = "tcpdump -r sdcard/whiff.pcap -nttttvv | wc -l";

                        try {
                            Runtime rt = Runtime.getRuntime();
                            Process proc = rt.exec(getPcapLines);

                            BufferedReader stdInput = new BufferedReader(new
                                    InputStreamReader(proc.getInputStream()));

                            BufferedReader stdError = new BufferedReader(new
                                    InputStreamReader(proc.getErrorStream()));

                        } catch (IOException e) {
                            Log.i("exception", e.toString());
                        }*/

                        readPcap[0] = "su";
                        readPcap[1] = "cd /system/bin";
                        readPcap[2] = "tcpdump -r sdcard/whiff.pcap -nttttvvXX | tr \"\n\" \"\n \"";

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
                                    temp = temp.substring(1, temp.length());
                                    if(isHex(temp) == true){
                                        stringToDB += "\n" + temp;
                                    }
                                    else {
                                        stringToDB += temp;
                                    }
                                }

                            }

                            if((stdInput.readLine()) == null){
                                parsePacket(stringToDB);
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
        String[] word = stringToDB.split(" ");
        String[] DateTime = getDateTime(word);
        String[] protocolInfo = checkProtocol(word);
        String[] ipAdd = getIP(word, protocolInfo);
        CapturePackets capturePackets = new CapturePackets(DateTime[0], DateTime[1], ipAdd[0], ipAdd[1], protocolInfo[0], protocolInfo[1], stringToDB);
        dbHandler.addPacket(capturePackets);
    }

    public String[] getDateTime(String[] word){
        String[] DateTime = new String[2];
        DateTime[0] = word[0];
        DateTime[1] = word[1];
        return DateTime;
    }

    public String[] checkProtocol(String[] word){
        String[] protoInfo = checkIfARP(word);
        if(protoInfo[0].equals("no")) {
            /*for (int i = 0; i < word.length; i++) {
                if (word[i].equals("proto")) {
                    protoInfo[0] = word[i + 1];
                    break;
                }
            }*/
            if (word[2].equals("IP")){
                if(word[13].equals("proto")){
                    protoInfo[0] = word[14];
                    String[] ports = getPort(word);
                    protoInfo[1] = "Source Port: " + ports[0] + "\n" + "Destination Port: " + ports[1];
                }
            }
        }
        return protoInfo;
    }

    //Check if protocol is ARP
    public String[] checkIfARP(String[] word){
        String[] ARPinfo = new String[2];
        if (word[2].equals("ARP,")){
            ARPinfo[0] = "ARP";
            ARPinfo[1] = "Reply";
            if(word[9].equals("Request")){
                if(word[10].equals("who-has")){
                    ARPinfo[1] = "Broadcast";
                }
            }
        }
        else {
            ARPinfo[0] = "no";
        }
        return ARPinfo;
    }

    public String[] getPort(String[] word){
        String[] ipPort = new String[2];
        Pattern ipPattern = Pattern.compile("(\\d{1,3})(\\.)(\\d{1,3})(\\.)(\\d{1,3})(\\.)(\\d{1,3}).*");
        int count = 0;

        for(int i=0;i<word.length;i++){
            Matcher ipMatcher = ipPattern.matcher(word[i]);
            if (ipMatcher.matches() == true){
                ipPort[count] = word[i];
                count++;
                if(count > 1){
                    break;
                }
            }
        }

        for(int i = 0; i < 2; i++) {
            String[] temp = ipPort[i].split(Pattern.quote("."));
            ipPort[i] = temp[4];
        }

        return ipPort;
    }

    public String[] getIP(String[] word, String[] ARPinfo){
        String[] ipAdd = new String[2];
        Pattern ipPattern = Pattern.compile("(\\d{1,3})(\\.)(\\d{1,3})(\\.)(\\d{1,3})(\\.)(\\d{1,3}).*");
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

        if(ARPinfo[0].equals("ARP") && ARPinfo[1].equals("Request")){
            ipAdd[0] = ipAdd[1].substring(0,ipAdd[1].length()-1);
            ipAdd[1] = "Broadcast";
        }
        else if(ARPinfo[0].equals("ARP")){
            String temp = ipAdd[0];
            ipAdd[0] = ipAdd[1].substring(0,ipAdd[1].length()-1);
            ipAdd[1] = temp;
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

        Pattern p1 = Pattern.compile("(\\d{4})(-)(\\d{2})(-)(\\d{2})");
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

    public boolean isHex(String line){
        String temp = line.split(" ")[0];
        Pattern p = Pattern.compile("(0x)(\\w{4})(:)");
        Matcher m = p.matcher(temp);
        boolean b = m.matches();
        return b;
    }
}


