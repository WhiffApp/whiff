package fyp.uow.whiff;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapNativeException;

public class scan_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
    }

    /* try {
        GetNextPacket g = new GetNextPacket();
    } catch (PcapNativeException e) {
        e.printStackTrace();
    } catch (NotOpenException e) {
        e.printStackTrace();
    } */

}
