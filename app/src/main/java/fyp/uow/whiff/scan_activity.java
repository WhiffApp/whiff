package fyp.uow.whiff;

import android.content.Intent;
import android.net.VpnService;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import fyp.uow.whiff.FileManager;
import jp.co.taosoftware.android.packetcapture.PacketCaptureService;

public class scan_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = VpnService.prepare(scan_activity.this);
                if (intent != null) {
                    startActivityForResult(intent, 0);
                } else {
                    onActivityResult(0, RESULT_OK, null);
                }
            }
        });

        FileManager.init();
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data) {

        if (result == RESULT_OK) {
            Boolean isRunning = PacketCaptureService.isRunning();
            Intent i = isRunning
                    ? getServiceIntent(PacketCaptureService.ACTION_STOP)
                    : getServiceIntent(PacketCaptureService.ACTION_START);

            if (isRunning == Boolean.FALSE) {
                i.putExtra(PacketCaptureService.PCAP_LOG_FILENAME, FileManager.createNewPacketFile().getPath());
            }

            startService(i);
        }
    }

    private Intent getServiceIntent(String action) {
        Intent i = new Intent(this, PacketCaptureService.class);
        i.setAction(action);
        return i;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
