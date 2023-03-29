package com.kristianjones.coutndownservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    static final String TAG = LocalService.class.getName();

    Boolean timerActive;

    Intent serviceIntent;

    Long millisUntilFinished;
    Long timeLeft;

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);

        timerActive = false;
        serviceIntent = new Intent(this,LocalService.class);
    }

    private BroadcastReceiver timerReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateTextView(intent);

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(timerReceiver, new IntentFilter(LocalService.countdownService));
        Log.i(TAG, "Registered broacast receiver");
    }


    @Override
    public void onStop() {
        try {
            unregisterReceiver(timerReceiver);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }
    @Override
    public void onDestroy() {
        stopService(serviceIntent);
        Log.i(TAG, "Stopped service");
        super.onDestroy();
    }

    // Got to here.
    /** Called when a button is clicked (the button in the layout file attaches to
     * this method with the android:onClick attribute) */
    public void onStartClick(View v) {

        Log.d(TAG,"onStartClick: " + String.valueOf(timerActive));

        if (timerActive) {
            serviceIntent.putExtra("TIME_LEFT",timeLeft);
        } else {
            timeLeft = Long.valueOf(30000);
            timerActive = true;
            serviceIntent.putExtra("TIME_LEFT",timeLeft);
            serviceIntent.putExtra("TIMER_ACTIVE",timerActive);
        }

        Log.d(TAG,"onStartClick: " + String.valueOf(timeLeft));

        startService(serviceIntent);
        Log.i(TAG,"Started Service");
    }

    public void onPauseClick(View v) {
        stopService(serviceIntent);
        Log.i(TAG,"Stopped Service");
    }

    private void updateTextView(Intent intent) {

        timeLeft = intent.getLongExtra("countdown", 0);

        if (timeLeft > 0) {
            Log.i(TAG, "Countdown seconds remaining: " +  timeLeft / 1000);
            textView.setText(String.valueOf(timeLeft/1000));

        } else {
            timerActive = false;
            textView.setText("No timer");
        }

    }

}