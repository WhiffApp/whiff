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

        Button mainButton = (Button)findViewById(R.id.button);

        mainButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {


                        String[] command = new String[3];
                        command[0] = "su";
                        command[1] = "cd /system/bin";
                        command[2] = "tcpdump -D";


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

                            TextView mainText = (TextView) findViewById(R.id.textView2);
                            mainText.setText(print);

                        } catch (IOException e) {
                            Log.i("exception", e.toString());
                        }
                    }
                });
    }
}
