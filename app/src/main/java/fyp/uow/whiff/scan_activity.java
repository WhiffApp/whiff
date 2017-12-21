package fyp.uow.whiff;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapNativeException;

public class scan_activity extends AppCompatActivity {

    //GetNextPacket.java
    private GetNextPacket mGetNextPacket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        //Start Button Settings
        Button startButton = findViewById(R.id.startButton);

        startButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        try {
                            mGetNextPacket = new GetNextPacket();
                        } catch (PcapNativeException e) {
                            e.printStackTrace();
                        } catch (NotOpenException e) {
                            e.printStackTrace();
                        }

                    }
                }
        );

    }

    /* try {
        GetNextPacket g = new GetNextPacket();
    } catch (PcapNativeException e) {
        e.printStackTrace();
    } catch (NotOpenException e) {
        e.printStackTrace();
    } */

}
