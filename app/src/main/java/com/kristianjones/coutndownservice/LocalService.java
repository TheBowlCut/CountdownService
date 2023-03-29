package com.kristianjones.coutndownservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import java.util.Random;

public class LocalService extends Service {
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    public static final String countdownService = "com.kristianjones.coutndownservice.LocalService";

    static final String TAG = LocalService.class.getName();

    CountDownTimer countDownTimer;

    Intent countdownIntent = new Intent(countdownService);

    Long timeLeft;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG,"Timer Started");

    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        LocalService getService() {
            // Return this instance of LocalService so clients can call public methods
            return LocalService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG,"InStartCommand");

        Bundle bundle = intent.getExtras();
        timeLeft = bundle.getLong("TIME_LEFT");

        Log.d(TAG,"TimeLeft: " + String.valueOf(timeLeft));

        countDownTimer = new CountDownTimer(timeLeft,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i(TAG,"Countdown seconds remaining: " + millisUntilFinished/1000);
                countdownIntent.putExtra("countdown",millisUntilFinished);
                sendBroadcast(countdownIntent);

            }

            @Override
            public void onFinish() {
                Log.i(TAG,"Timer finished");
                countdownIntent.putExtra("countdown",0);
                sendBroadcast(countdownIntent);
            }
        }.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        countDownTimer.cancel();
        super.onDestroy();
    }
}