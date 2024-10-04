package com.branddrop.bakit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * This foreground service continuously fetch location updates and send the data to server.
 */
public class LocationUpdatesService extends Service {

    private static final String PACKAGE_NAME =
            "com.google.android.gms.location.sample.locationupdatesforegroundservice";

    private static final String TAG = LocationUpdatesService.class.getSimpleName();

    /**
     * The name of the channel for notifications.
     */
    private static final String CHANNEL_ID = "LocationUpdates";


    private final IBinder mBinder = new LocalBinder();


    /**
     * The identifier for the notification displayed for the foreground service.
     */
    private static final int NOTIFICATION_ID = 12345678;


    private NotificationManager mNotificationManager;

    /**
     * Contains parameters used by {@link com.google.android.gms.location.FusedLocationProviderApi}.
     */
    private LocationRequest mLocationRequest;

    /**
     * Provides access to the Fused Location Provider API.
     */
    public FusedLocationProviderClient mFusedLocationClient;

    public static final long UPDATE_INTERVAL = 1 * 1000;
    public static final float SMALLEST_DISPLACEMENT = 1.0F;
    public static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 1;
    public String appName;
    public static String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";
    public static String ACTION_START_SERVICE = "ACTION_START_SERVICE";
    private ActivityRecognitionClient mActivityRecognitionClient;

    public LocationUpdatesService() {
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        BrandDrop brandDrop = new BrandDrop(this);

        mActivityRecognitionClient = ActivityRecognition.getClient(this);

        createLocationRequest();

        /*try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, getPendingIntent());
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        startActivityUpdates();

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_NONE);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    private void startActivityUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mActivityRecognitionClient.requestActivityUpdates(3, getPendingIntent())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Activity recognition requested"))
                .addOnFailureListener(e -> Log.e(TAG, "Activity recognition request failed", e));
    }

    public void startLocationUpdates() {
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, getPendingIntent());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(getPendingIntent());
    }

    // This method sets the attributes to fetch location updates.
    private void createLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT);

        /*mLocationRequest = new LocationRequest.Builder(UPDATE_INTERVAL)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateDistanceMeters(SMALLEST_DISPLACEMENT)
                .build();*/
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(getApplicationContext(), LocationUpdatesBroadcastReceiver.class);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        } else {
            return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getExtras() != null) {
            appName = (String) Objects.requireNonNull(intent.getExtras()).getString("appName");

            int type = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                type = ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION;
            }
            ServiceCompat.startForeground(
                    this,
                    NOTIFICATION_ID,
                    getNotification(appName),
                    type
            );
        }

  /*      if (ACTION_STOP_SERVICE.equals(intent.getAction())) {
            Log.d(TAG, "called to cancel service");
            mNotificationManager.cancel(NOTIFICATION_ID);
//            stopSelf();
        }*/

        if (ACTION_STOP_SERVICE.equals(intent.getAction())) {
            Log.d(TAG, "called to cancel service");
            stopLocationUpdates();
            mNotificationManager.cancel(NOTIFICATION_ID);
//            stopSelf();
        } else if (ACTION_START_SERVICE.equals(intent.getAction())) {
            startLocationUpdates();
        }
        // Tells the system to not try to recreate the service after it has been killed.
        return START_NOT_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        //    stopForeground(true);
        super.onDestroy();
    }

    /**
     * Returns the {@link NotificationCompat} used as part of the foreground service.
     */
    private Notification getNotification(String appname) {
        int resourceId;
        try {
            resourceId = getResources().getIdentifier(getResources().getString(R.string.brand_drop_foreground_icon), "drawable", getPackageName());
        } catch (Exception e) {
            resourceId = R.mipmap.ic_launcher_round;
        }
        Intent stopSelf = new Intent(this, LocationUpdatesService.class);
        stopSelf.setAction(ACTION_STOP_SERVICE);
        PendingIntent pStopSelf;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pStopSelf = PendingIntent.getService(this, 0, stopSelf, PendingIntent.FLAG_MUTABLE);

        } else {
            pStopSelf = PendingIntent.getService(this, 0, stopSelf, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        }

        Notification.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder = new Notification.Builder(this)
                    .setContentText(appname + " " + getResources().getString(R.string.brand_drop_foreground_message))
                    .setOngoing(true).addAction(R.mipmap.ic_launcher_round, "Stop service", pStopSelf)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSmallIcon(resourceId)
                    .setColor(getResources().getColor(R.color.notification_icon_color))
                    .setWhen(System.currentTimeMillis());

        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            builder.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE);
        }

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }
        return builder.build();
    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        LocationUpdatesService getService() {
            return LocationUpdatesService.this;
        }
    }
}
