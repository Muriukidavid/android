package co.karibe.BLEToolBox;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import static co.karibe.BLEToolBox.R.id.toolbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "co.karibe.BLEToolBox.MESSAGE";

    private void requestPermissions(){
        int androidVersion = Build.VERSION.SDK_INT;

        if (androidVersion >= 23){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                    }, 1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        Toolbar mToolbar = (Toolbar)findViewById(toolbar);
        mToolbar.setTitle("Kinetis BLE Toolbox");
        setSupportActionBar(mToolbar);
        final View pressure = findViewById(R.id.bloodPressure);

        requestPermissions();
        pressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Display.class);
                String message = "blood_pressure";
                intent.putExtra(EXTRA_MESSAGE,message);
                startActivity(intent);
            }
        });
        final View heart_rate = findViewById(R.id.heartRate);
        heart_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Display.class);
                String message = "heart_rate";
                intent.putExtra(EXTRA_MESSAGE,message);
                startActivity(intent);
            }
        });
        final View flex_sensors = findViewById(R.id.flexSensors);
        flex_sensors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Display.class);
                String message = "flex_sensors";
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