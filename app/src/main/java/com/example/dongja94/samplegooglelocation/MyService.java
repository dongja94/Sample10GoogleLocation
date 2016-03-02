package com.example.dongja94.samplegooglelocation;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        DetectedActivity detected = result.getMostProbableActivity();
        if (detected.getType() == DetectedActivity.IN_VEHICLE) {
            // ....
        }
        return Service.START_NOT_STICKY;
    }
}
