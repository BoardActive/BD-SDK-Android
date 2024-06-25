package com.branddrop.addrop.activity;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.Manifest;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.branddrop.bakit.BrandDrop;
import com.branddrop.bakit.RequestPermissionActivity;
import com.branddrop.bakit.customViews.CustomAttributesActivity;
import com.branddrop.addrop.R;
import com.branddrop.addrop.utils.LocationService;
import com.branddrop.bakit.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.Strictness;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();

    private static final int NOTIFICATION_PERMISSION_CODE = 123;

    private View parent_view;
    private static final String IS_FOREGROUND = "isforeground";
    //Add the BoardActive Object
    private BrandDrop mBrandDrop;
    private Button btn_userAttributes,
            btn_customAttributes,
            btn_getMe,
            btn_messages;
    private EditText httpReponse;
    private ToggleButton btnService;
    public final static String BAKIT_URL = "BAKIT_URL";
    private boolean mBounded;
    private LocationService locationService;
    final static int REQUEST_CODE = 1;
    private PendingIntent geofencePendingIntent;
    BroadcastReceiver br = new com.branddrop.bakit.GeofenceBroadCastReceiver();
    HashMap<String, Object> updatedCustomAttributes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.d(TAG, "[BrandDropApp] onCreate()");

        httpReponse = (EditText) findViewById(R.id.httpResponse);

        checkNotificationPermission();

        btn_userAttributes();
        btn_messages();
        btn_customAttributes();
        btn_service();
        init();
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("MYTAG", "This is your Firebase token" + token);

    }

    private void checkNotificationPermission() {
        if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(this, new String[]{POST_NOTIFICATIONS}, 1002);
            }
        }
    }

    ProgressDialog progressDialog;

    private void btn_service() {
        btnService = findViewById(R.id.btn_service);

        btnService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isForeground) {
                initHandler();
                progressDialog = new ProgressDialog(MainActivity.this, R.style.progressDialogTheme);
                progressDialog.setCancelable(false);
                progressDialog.show();
                ////      mBoardActive.checkLocationPermissions();
                // mBoardActive.setIsForeground(isForeground);
                // mBoardActive.initialize();
            }
        });
    }

    private void initHandler() {
        Handler delayHandler = new Handler();
        delayHandler.postDelayed(new Runnable() {
            public void run() {
                handlerCallback();
            }
        }, 5000);
    }

    private void handlerCallback() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent mIntent = new Intent(this, LocationService.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);

    }

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(getApplicationContext(), "Service is disconnected", Toast.LENGTH_SHORT).show();
            mBounded = false;
            locationService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(getApplicationContext(), "Service is connected", Toast.LENGTH_SHORT).show();
            mBounded = true;
            LocationService.LocalBinder mLocalBinder = (LocationService.LocalBinder) service;
            MainActivity.this.locationService = mLocalBinder.getService();
        }
    };

    public void init() {
        // Create an instant of BoardActive
        mBrandDrop = new BrandDrop(MainActivity.this);


        // Add URL to point to BoardActive REST API
        //mBoardActive.setAppUrl(BoardActive.APP_URL_PROD); // Production
        mBrandDrop.setAppUrl(BrandDrop.APP_URL_DEV); // Development

        // Add AppID provided by BoardActive
        // mBoardActive.setAppId("ADD_APP_ID");

//        mBrandDrop.setAppId("346");

        // Keyur :
        mBrandDrop.setAppId("2403");
//        mBrandDrop.setAppKey("be1ee020-8d08-40e8-a3ea-d028d5533519");
        mBrandDrop.setAppKey("77dbdde2-4361-47a0-8fb2-821d981cfd35");


        // Add AppKey provided by BoardActive
//        mBoardActive.setAppKey("ADD_APP_KEY");
        // mBoardActive.setAppKey("ef748553-e55a-4cb4-b339-7813e395a5b1");
        //mBoardActive.setAppKey("88fd530b-c111-4077-a1d3-ad0a24b127fd");
        //    mBoardActive.setAppKey("f3b64b8b-84e7-4eae-869a-9b9da7981725");
        //mBoardActive.setAppKey("d17f0feb-4f96-4c2a-83fd-fd6302ae3a16");
        // mBoardActive.setAppKey("f6947f91-740f-4ce2-9620-73a91316d289");
        // mBoardActive.setAppKey("63e93e07-7ee5-4491-91e9-e2ab93786646");
//        mBrandDrop.setAppKey("fe8c3310-498c-4fd0-b3df-ea430d9a8084");
        //   mBoardActive.setAppKey("355cd7b8-e355-4b07-916c-67a4eb2360ab");
        //mBoardActive.setAppKey("474c7aef-83fd-411e-8a83-e781ef5f3dff");
        // mBoardActive.setAppKey(BoardActive.BAKIT_APP_KEY);

        // Add the version of your App
        mBrandDrop.setAppVersion("1.0.0");

        // Optional, set to 'true' to run in foreground
        mBrandDrop.setIsForeground(true);
        mBrandDrop.StartWorker(getResources().getString(R.string.app_name));

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                            Manifest.permission.FOREGROUND_SERVICE_LOCATION,
                    }, REQUEST_CODE);
            Log.e(TAG, "onCreate: permission denied");
            mBrandDrop.isPermissionGranted = false;

        } else {
            Log.e(TAG, "onCreate: permission granded");
            mBrandDrop.isPermissionGranted = true;
            //mBoardActive.getLocationList(false);

        }
        updatedCustomAttributes = new HashMap<>();
        updatedCustomAttributes.put("braves_fan", true);
        mBrandDrop.putCustomAtrributes(new BrandDrop.PutMeCallback() {
            @Override
            public void onResponse(Object value) {
            }
        }, updatedCustomAttributes);

        // Get Firebase Token
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "[BrandDropApp] getInstanceId failed", task.getException());
                            return;
                        }

                        String fcmToken = task.getResult().getToken();

                        Log.d(TAG, "[BrandDropApp] fcmToken: " + fcmToken);

                        // Add Firebase Token to BoardActive
                        mBrandDrop.setAppToken(fcmToken);

                        mBrandDrop.setIsForeground(true);

                        // Check for Location permissions
                        mBrandDrop.checkLocationPermissions();
                        mBrandDrop.checkNotificationPermissions();

                        // Initialize BoardActive
                        mBrandDrop.initialize();

                        // Register the device with BoardActive
                        //if(mBoardActive.isAppEnabled)
                        // {
//                        Log.e("list",""+mBoardActive.getLocationArrayList().size());
//                        mBoardActive.postLogin(new BoardActive.PostLoginCallback() {
//                            @Override
//                            public void onResponse(Object value) {
//                                if(mBoardActive.latitude != 0.0 && mBoardActive.longitude != 0.0){
//                                    Constants.FIRST_TIME_GET_GEOFENCE=true;
//                                    mBoardActive.getLocationList();
//                                }else {
//                                    Constants.FIRST_TIME_GET_GEOFENCE=false;
//                                }
//
//                            }
//                        }, "taylor@boardactive.com", "000000");

                        try {
                            mBrandDrop.registerDevice(new BrandDrop.PostRegisterCallback() {
                                @Override
                                public void onResponse(Object value) {
                                    Log.d(TAG, value.toString());
                                    Gson gson = new GsonBuilder().setStrictness(Strictness.LENIENT).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                                    //   Me me = gson.fromJson(value.toString(), Me.class);
                                    try {
                                        JsonParser parser = new JsonParser();
                                        JsonElement je = parser.parse(value.toString());
                                        httpReponse.setText(gson.toJson(je));
                                        Log.d(TAG, gson.toJson(je));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        registerReceiver();
    }
    /* register receiver for geofence trigger*/

    public void registerReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        ContextCompat.registerReceiver(this, br, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
//        this.registerReceiver(br, filter);
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.registerReceiver(this, br, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
        } else {
            this.registerReceiver(br, filter);
        }*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_READ_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mBrandDrop.setupLocationRequest();

                } else {
                    // Permission Denied
                    Toast.makeText(this, "location denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case 1002:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!shouldShowRequestPermissionRationale(permissions[0])) { // Need Show Rationale
                            checkNotificationPermission();
                        }
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }

    public void btn_messages() {

        btn_messages = (Button) findViewById(R.id.btn_messages);

        btn_messages.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getBaseContext(), MessagesActivity.class);
                startActivity(intent);
            }

        });

    }

    public void btn_userAttributes() {

        btn_userAttributes = (Button) findViewById(R.id.btn_userAttributes);

        btn_userAttributes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getBaseContext(), UserActivity.class);
                startActivity(intent);
            }

        });

    }

    public void btn_customAttributes() {

        btn_customAttributes = (Button) findViewById(R.id.btn_customAttributes);

        btn_customAttributes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
//                Intent intent = new Intent(getBaseContext(), CustomActivity.class);
                Intent intent = new Intent(getBaseContext(), CustomAttributesActivity.class);
                intent.putExtra("baseUrl", BrandDrop.APP_URL_DEV);
                startActivity(intent);
            }

        });

    }

    public void cancelService() {
        Intent intent = new Intent(this, LocationService.class);
        stopService(intent);
        //mBoardActive.removeGeofence(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (br != null) {
            unregisterReceiver(br);

        }
        if (mConnection != null && mBounded) {
            this.unbindService(mConnection);

        }
        //  mBoardActive.stop();
        cancelService();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if(br != null){
//            unregisterReceiver(br);
//
//        }
        if (mConnection != null && mBounded) {
            this.unbindService(mConnection);

        }
        //mBoardActive.stop();
    }


}
