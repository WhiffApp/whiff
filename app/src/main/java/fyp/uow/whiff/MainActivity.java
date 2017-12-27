package fyp.uow.whiff;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Scan Button Settings
        Button scanButton = findViewById(R.id.scanButton);

        scanButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        Intent i=new Intent(MainActivity.this, scan_activity.class);
                        startActivity(i);
                    }
                }
        );

        //Help Button Settings
        Button helpButton = findViewById(R.id.helpAndFaqButton);

        helpButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        Intent i=new Intent(MainActivity.this, help_activity.class);
                        startActivity(i);
                    }
                }
        );

        //Exit Button Settings
        Button exitButton  = findViewById(R.id.exitButton);

        exitButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        int pid = android.os.Process.myPid();
                        android.os.Process.killProcess(pid);
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        startActivity(intent);
                    }
                }
        );

    }
}
