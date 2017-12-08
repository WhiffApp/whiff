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
