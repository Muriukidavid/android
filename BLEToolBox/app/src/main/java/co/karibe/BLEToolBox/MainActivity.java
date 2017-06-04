package co.karibe.BLEToolBox;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import static co.karibe.BLEToolBox.R.id.toolbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "co.karibe.BLEToolBox.MESSAGE";
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_layout);
        mToolbar = (Toolbar)findViewById(toolbar);
        mToolbar.setTitle("Kinetis BLE Toolbox");
        setSupportActionBar(mToolbar);
        final View pressure = findViewById(R.id.bloodPressure);
        pressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Display.class);
                String message = "pressure";
                intent.putExtra(EXTRA_MESSAGE,message);
                startActivity(intent);
            }
        });
        final View heartrate = findViewById(R.id.heartRate);
        heartrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Display.class);
                String message = "heartrate";
                intent.putExtra(EXTRA_MESSAGE,message);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onResume(){
        super.onResume();
    }
    @Override
    protected void onPause(){
        super.onPause();
        Log.w("onPause","App paused");
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
}