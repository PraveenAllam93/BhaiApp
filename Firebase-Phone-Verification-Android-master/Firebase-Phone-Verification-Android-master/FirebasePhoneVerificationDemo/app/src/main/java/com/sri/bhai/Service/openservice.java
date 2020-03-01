package com.sri.bhai.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.sri.bhai.Notification_screen;

public class openservice extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Intent intent1 = new Intent(this, Notification_screen.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent1);


    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
}
