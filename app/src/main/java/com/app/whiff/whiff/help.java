package com.app.whiff.whiff;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class help extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Button devButton = (Button)findViewById(R.id.getDevButton);
        devButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        String[] listDev = new String[3];
                        listDev[0] = "su";
                        listDev[1] = "cd /system/bin";
                        listDev[2] = "tcpdump -D";

                        TextView mainText = (TextView) findViewById(R.id.textView2);
                        mainText.setText(callCmd(listDev));
                    }
                });

        Button pcapButton = (Button)findViewById(R.id.pcapButton);
        pcapButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        String[] pcap = new String[3];
                        pcap[0] = "su";
                        pcap[1] = "cd /system/bin";
                        pcap[2] = "tcpdump -v -s 0 -c 5";

                        TextView mainText = (TextView) findViewById(R.id.textView2);
                        mainText.setText(callCmd(pcap));
                    }
                });
    }

    public String callCmd(String[] command){

        String output = "";

        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(command);

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()));

            String temp = null;
            String print = null;
            while ((temp = stdInput.readLine()) != null) {
                if (print == null){
                    print = temp + "\n";
                }
                else{
                    print = print + temp + "\n";
                }
            }
            while ((temp = stdError.readLine()) != null) {
                print = print + temp;
            }

            output = print;

        } catch (IOException e) {
            Log.i("exception", e.toString());
        }

        return output;
    }
}


