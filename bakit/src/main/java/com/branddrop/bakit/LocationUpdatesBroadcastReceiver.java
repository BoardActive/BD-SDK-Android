package com.branddrop.bakit;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.work.WorkManager;

import com.branddrop.bakit.Tools.SharedPreferenceHelper;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LocationUpdatesBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_PROCESS_UPDATES = "PROCESS_UPDATES";
    public static final String TAG = LocationUpdatesBroadcastReceiver.class.getName();
    public final static String LAST_DATA_UPDATED_TIME = "last_data_updated_time";
    private static final String IS_FOREGROUND = "isforeground";
    private BrandDrop mBrandDrop;
    private Context context;
    public static Location firstLocation;
    int count = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if (intent != null) {
            mBrandDrop = new BrandDrop(context.getApplicationContext());
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {

                getLocationUpdates(context, intent);

                ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
                if (result != null) {
                    Log.d(TAG, "Detected activity: " + result.getMostProbableActivity().getType());
                    handleActivityResult(context, result.getMostProbableActivity());
                }
            }
        }
    }

    private void handleActivityResult(Context context, DetectedActivity result) {
        Intent serviceIntent = new Intent(context, LocationUpdatesService.class);
        if (result.getType() == DetectedActivity.STILL) {
            Log.d(TAG, "Device is still, stopping location updates");
            serviceIntent.setAction(LocationUpdatesService.ACTION_STOP_SERVICE);
        } else {
            Log.d(TAG, "Device is moving, continuing location updates");
            // Start service without any action, which will continue location updates
            serviceIntent.setAction(LocationUpdatesService.ACTION_START_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }

    @SuppressLint("MissingPermission")
    public void getLocationUpdates(final Context context, final Intent intent) {
        // Log.e("enter into lcoation","enter");

        LocationResult result = LocationResult.extractResult(intent);
        if (result != null) {

            List<Location> locations = result.getLocations();

            if (locations.size() > 0) {
                firstLocation = locations.get(0);
                Log.e("firstlocation", "lat ===> " + firstLocation.getLatitude() + "long ===> " + firstLocation.getLongitude());
//                if (mBoardActive.getPastLongitude() == null && mBoardActive.getPastLatitude() == null) {
//                    mBoardActive.setPastLatitude(firstLocation.getLatitude());
//                    mBoardActive.setPastLongitude(firstLocation.getLongitude());
//                }
//                if(mBoardActive.getPastLatitude() != null && mBoardActive.getPastLongitude() != null)
//                {
//                    if (Double.parseDouble(mBoardActive.getPastLatitude()) != firstLocation.getLatitude() && Double.parseDouble(mBoardActive.getPastLongitude()) != firstLocation.getLongitude()) {
//                        if (!Constants.FIRST_TIME_GET_GEOFENCE) {
//                            Constants.FIRST_TIME_GET_GEOFENCE = true;
//                            mBoardActive.getLocationList();
//                        }
//                        Location temp = new Location(LocationManager.GPS_PROVIDER);
//                        temp.setLatitude(Double.parseDouble(mBoardActive.getPastLatitude()));
//                        temp.setLongitude(Double.parseDouble(mBoardActive.getPastLongitude()));
//                        Log.e("distance", "" + firstLocation.distanceTo(temp));
//
//                        Log.d(TAG, "PassLoc lat/lng: " + mBoardActive.getPastLatitude() + " " + mBoardActive.getPastLongitude());
//                        if (firstLocation.distanceTo(temp) > Constants.DISTANCE) {
//                            //setPastLongitude(null);
//                            // setPastLatitude(null);
//                            mBoardActive.setPastLatitude(firstLocation.getLatitude());
//                            mBoardActive.setPastLongitude(firstLocation.getLongitude());
//                            Log.e("new lat", mBoardActive.getPastLatitude());
//                            Log.e("new lat", mBoardActive.getPastLongitude());
//                            Log.e("enter into distance", "enter into distance");
//                            mBoardActive.setLocationArrayList(null);
//                            mBoardActive.getLocationList();
//
//                        }
//
//                    }
//
//                }

//                if(mBoardActive.getPastLatitude() != null && mBoardActive.getPastLongitude() != null ) {
//                    Location temp = new Location(LocationManager.GPS_PROVIDER);
//                    temp.setLatitude(Double.parseDouble(mBoardActive.getPastLatitude()));
//                    temp.setLongitude(Double.parseDouble(mBoardActive.getPastLongitude()));
//                    if (firstLocation != null) {
//                        Log.e("distance", "" + firstLocation.distanceTo(temp));
//                        if (firstLocation.distanceTo(temp) > 1000) {
//                            mBoardActive.setPastLatitude(null);
//                            mBoardActive.setPastLongitude(null);
//
//                            mBoardActive.setPastLatitude(firstLocation.getLatitude());
//                            mBoardActive.setPastLongitude(firstLocation.getLongitude());
//                            Log.e("new lat",mBoardActive.getPastLatitude());
//                            Log.e("new lat",mBoardActive.getPastLatitude());
//
//                            Log.e("distance1", "" + firstLocation.distanceTo(temp));
//
//                            Log.e("enter into distance", "enter into distance");
//                            mBoardActive.setLocationArrayList(null);
//                           // Log.e("shared after distance",""+mBoardActive.getLocationArrayList().size());
//                            mBoardActive.getLocationList(true);
//
//                        }
//                    }
//                }

//                if(mBoardActive != null &&  mBoardActive.getCurrentLocationArrayList() != null){
//                    if(mBoardActive.getCurrentLocationArrayList().size() > 0 && !mBoardActive.getCurrentLocationArrayList().isEmpty())
//                    {
//                        for(int i=0; i< mBoardActive.getCurrentLocationArrayList().size();i++)
//                        {
//                            mBoardActive.previousLocation = mBoardActive.getCurrentLocationArrayList().get(i);
//                        }
//
//                    }
//
//                }
//                if(mBoardActive != null){
//                    if(mBoardActive.previousLocation == null){
//                        mBoardActive.previousLocation = firstLocation;
//                        saveLocation(firstLocation);
//                    }else if(mBoardActive.previousLocation.distanceTo(firstLocation) > 2000)
//                    {
//                        mBoardActive.previousLocation = firstLocation;
//                        saveLocation(firstLocation);
//
//                    }
//                }
                updateLocation(context, firstLocation);
            }
        } else {
            // An additional check to start worker by checking the location permissions in case locations are not being retrieved.
            if (PermissionExceptionHandler.with(context).wantToStartWorker()) {
                if (!SharedPreferenceHelper.getBoolean(context, IS_FOREGROUND, false))
                    startWorker();
            } else {
                WorkManager.getInstance(context).cancelAllWork();
            }
        }
    }

    public void saveLocation(Location location) {
        ArrayList<Location> currrentLocationArrayList = new ArrayList();
        if (mBrandDrop.getCurrentLocationArrayList() != null) {
            currrentLocationArrayList = mBrandDrop.getCurrentLocationArrayList();
            currrentLocationArrayList.add(location);
            mBrandDrop.setCurrentLocationArrayList(currrentLocationArrayList);
        } else {
            currrentLocationArrayList.add(location);
            mBrandDrop.setCurrentLocationArrayList(currrentLocationArrayList);
        }
    }

    // This method starts the worker if locations are not being retreived.
    private void startWorker() {
        Intent intent = new Intent(context, MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context.getApplicationContext(), 234324243, intent, PendingIntent.FLAG_MUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() +
                2 * 60 * 1000, pendingIntent);
    }

    // This method sends the data to server and displays a local notification of the location
    private void updateLocation(final Context context, final Location firstLocation) {
        DateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss Z (zzzz)");
        final String date = df.format(Calendar.getInstance().getTime());

        //sends data to server every minute.
//        if (SharedPreferenceHelper.getLong(context, LAST_DATA_UPDATED_TIME, 0) == 0 || System.currentTimeMillis() - SharedPreferenceHelper.getLong(context, LAST_DATA_UPDATED_TIME, 0) >= 60000) {
//            mBoardActive.postLocation(new BoardActive.PostLocationCallback() {
//                @Override
//                public void onResponse(Object value) {
//                    Log.d(TAG, "[BAKit] onResponse" + value.toString());
//                    SharedPreferenceHelper.putLong(context, LAST_DATA_UPDATED_TIME, System.currentTimeMillis());
//                }
//            }, firstLocation.getLatitude(), firstLocation.getLongitude(), date);
//        }
    }

}
