package com.app.whiff.whiff;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.stericson.RootTools.RootTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
    String hex, ascii = "";

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
        devSpinner = findViewById(R.id.devSpinner);
        devSpinner.setAdapter(devList);

        //For DB Use
        dbHandler = new DBHandler(this, null, null, 1);

        final TextView mainText = findViewById(R.id.textView2);
        mainText.setMovementMethod(new ScrollingMovementMethod());

        Button pcapButton = findViewById(R.id.pcapButton);
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
                                if(isNewRow){
                                    if(count == 1){
                                        stringToDB = temp;
                                        count++;
                                        hex = "";
                                        ascii = "";
                                    }
                                    else{
                                        parsePacket(stringToDB, hex, ascii);
                                        stringToDB = temp;
                                        count++;
                                        hex = "";
                                        ascii = "";
                                    }
                                }
                                else{
                                    temp = temp.substring(1, temp.length());
                                    if(isHex(temp)) {
                                        hex += getHex(temp) + "\n";
                                        ascii += getAscii(temp) + "\n";
                                        stringToDB += "\n" + temp;
                                    } else {
                                        stringToDB += temp;
                                    }
                                }

                            }

                            if((stdInput.readLine()) == null){
                                parsePacket(stringToDB, hex, ascii);
                            }

                            if ((temp = stdError.readLine()) != null) {
                                mainText.setText(temp);
                            }

                        } catch (IOException e) {
                            Log.i("exception", e.toString());
                        }

                        mainText.setText(dbHandler.databaseToString());
                    }
                });

        Button dropDBButton = findViewById(R.id.dropDBButton);
        dropDBButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        dbHandler.dropDB();
                        mainText.setText("Database Cleared!");
                        mainText.scrollTo(0,0);
                    }
                }
        );
    }

    //Parse Packet to DB
    public void parsePacket(String stringToDB, String hex, String ascii){
        String[] word = stringToDB.split(" ");
        String[] DateTime = getDateTime(word);
        String[] protocolInfo = checkProtocol(word);
        String[] ipAdd = getIP(word, protocolInfo);
        CapturePackets capturePackets = new CapturePackets(DateTime[0], DateTime[1], ipAdd[0], ipAdd[1], protocolInfo[0], protocolInfo[1], hex, ascii, stringToDB);
        dbHandler.addPacket(capturePackets);
    }

    //Get Date and TimeStamp
    public String[] getDateTime(String[] word){
        String[] DateTime = new String[2];
        DateTime[0] = word[0];
        DateTime[1] = word[1];
        return DateTime;
    }

    //Get Hex Values
    public String getHex(String temp){
        String output = "";
        String[] hex = temp.split(" ");
        for (int i = 0; i < (hex.length-1); i++){
            output += hex[i] + "\t";
        }
        return output;
    }

    //Get Ascii Values
    public String getAscii(String temp){
        String[] hex = temp.split(" ");
        return hex[hex.length-1];
    }

    //Check Protocol
    public String[] checkProtocol(String[] word){
        String[] protoInfo = checkIfARP(word);
        if(protoInfo[0].equals("no")) {
            if (word[2].equals("IP")){
                if(word[13].equals("proto")){
                    protoInfo[0] = word[14];
                    String[] ports = getPort(word);
                    protoInfo[1] = "Source Port: " + ports[0] + "\n" + "Destination Port: " + ports[1].substring(0,ports[1].length()-1) + "\n" +
                            "Time To Live: " + word[6].substring(0,word[6].length()-1) + "\n" + "Identification: " + word[8].substring(0,word[8].length()-1) +
                            "\n" + "Offset: " + word[10].substring(0,word[10].length()-1) + "\n" + "Flags: " + word[12].substring(1,word[12].length()-2) +
                            "\n" + "Protocol: " + word[14] + "\n" + "Length: " + word[17].substring(0,word[17].length()-1);
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
            ARPinfo[1] = "Hardware Type: " + word[3] + "\n" + "Protocol Type: " + word[6] + "\n" +
                    "Hardware Size: " + word[5].substring(0,word[5].length()-2) + "\n" + "Hardware Size: " + word[8].substring(0,word[8].length()-2) + "\n" + "Length: " + getARPLength(word);
        }
        else {
            ARPinfo[0] = "no";
        }
        return ARPinfo;
    }

    public String getARPLength(String[] word){
        if(word[9].equals("Reply")){
            return word[14].split("\\n")[0];
        }
        else{
            return word[15].split("\\n")[0];
        }
    }

    //Get port number
    public String[] getPort(String[] word){
        String[] ipPort = new String[2];
        Pattern ipPattern = Pattern.compile("(\\d{1,3})(\\.)(\\d{1,3})(\\.)(\\d{1,3})(\\.)(\\d{1,3}).*");
        int count = 0;

        for(int i=0;i<word.length;i++){
            Matcher ipMatcher = ipPattern.matcher(word[i]);
            if (ipMatcher.matches()){
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

    //Get IP Address
    public String[] getIP(String[] word, String[] ARPinfo){
        String[] ipAdd = new String[2];
        Pattern ipPattern = Pattern.compile("(\\d{1,3})(\\.)(\\d{1,3})(\\.)(\\d{1,3})(\\.)(\\d{1,3}).*");
        int count = 0;

        if(ARPinfo[0].equals("ARP") && word[9].equals("Reply")){
            ipAdd[0] = word[10];
            ipAdd[1] = "Reply";
        }
        else {
            for (int i = 0; i < word.length; i++) {
                Matcher ipMatcher = ipPattern.matcher(word[i]);
                if (ipMatcher.matches()) {
                    ipAdd[count] = word[i];
                    count++;
                    if (count > 1) {
                        break;
                    }
                }
            }
            for (int i = 0; i < 2; i++) {
                ipAdd[i] = convertToIP(ipAdd[i]);
            }
            if (ARPinfo[0].equals("ARP") && word[9].equals("Request")) {
                ipAdd[0] = ipAdd[1].substring(0, ipAdd[1].length() - 1);
                ipAdd[1] = "Broadcast";
            }
        }
        return ipAdd;
    }

    //Convert string to IP
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

        if(!b1 && !b2){
            return (line.split(" ")[2]).equals("ARP,");
        }
        else{
            return true;
        }
    }

    //Check if new line is packet info in hexadecimal
    public boolean isHex(String line){
        String temp = line.split(" ")[0];
        Pattern p = Pattern.compile("(0x)(\\w{4})(:)");
        Matcher m = p.matcher(temp);
        boolean b = m.matches();
        return b;
    }
}


