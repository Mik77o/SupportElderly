package com.example.supportelderly;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class BatteryActivity extends AppCompatActivity {

    TextView battery_state_indicator;
    ImageView battery_icon;

    Handler handle;
    Runnable thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);

        battery_state_indicator = (TextView) findViewById(R.id.battery_state_indicator);
        battery_icon = (ImageView) findViewById(R.id.battery_icon);

        thread = new Runnable() {
            @Override
            public void run() {
                int lev = (int) batteryStateCount();

                battery_state_indicator.setText("BATERIA: " + lev + "%");

                if (lev > 75) {
                    battery_icon.setImageResource(R.drawable.battery_100);
                }
                if (lev > 50 && lev <= 75) {
                    battery_icon.setImageResource(R.drawable.battery_75);
                }
                if (lev > 25 && lev <= 50) {
                    battery_icon.setImageResource(R.drawable.battery_50);
                }
                if (lev > 5 && lev <= 25) {
                    battery_icon.setImageResource(R.drawable.battery_25);
                }
                if (lev < 5) {
                    battery_icon.setImageResource(R.drawable.battery_5);
                }
                handle.postDelayed(thread, 5000);
            }
        };

        handle = new Handler();
        handle.postDelayed(thread, 0);
    }

    public float batteryStateCount() {
        Intent batIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int levelOfBattery = batIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        if (levelOfBattery == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float) levelOfBattery / (float) scale) * 100.0f;
    }
}
