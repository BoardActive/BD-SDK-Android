# BrandDrop SDK Android

<img src="https://github.com/BoardActive/BD-SDK-Android/assets/108806504/32d7a6b0-afff-4bbc-ab9b-990c3fd27848?s=200&v=4" width="96" height="96"/>

### Location-Based Engagement
- Enhance your app. Empower your marketing.
    
- It's not about Advertising... It's about "PERSONALIZING"

- BrandDrop's platform connects brands to consumers using location-based engagement.

- Our international patent-pending Visualmatic™ software is a powerful marketing tool allowing brands to set up a virtual perimeter around any location, measure foot-traffic, and engage users with personalized messages when they enter geolocations

- Effectively attribute campaign efficiency by seeing where users go after the impression! 

- Use your BoardActive account to create Places (geo-fenced areas) and Messages (notifications) to deliver custom messages to your app users.


[Click Here to get a BranDrop account](https://app.branddrop.us/login)

The BrandDrop SDK will use a device's location to know when an app user passes into a geo-fence. Passing into a geo-fence can trigger an event allowing you to deliver notifications to your app users.  

## Installing the BranDrop SDK

BrandDrop for Android supports APK 15 and greater. 

To get the most our of the BrandDrop SDK your app will need to allow location permissions. 

To use the BrandDrop SDK your will need to implement our library. 

### SDK
Include the BrandDrop SDK into your project with JitPack repository.

### Dependency

We use JitPack as a repository service, you will add a few lines to the gradle files to import our SDK into your project. (Instructions for Maven, sbt, and leiningen available upon request)

```java
// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
    	classpath 'com.android.tools.build:gradle:8.1.4'
    	classpath 'com.google.gms:google-services:4.4.2' 
    }
}

// Add JitPack repository to top level build.gradle
	allprojects {
		google()
        	mavenCentral()
			repositories {
				...
				maven { url 'https://jitpack.io' }
		}
	}
```

Include the following to your App-level build.gradle

```java
...
dependencies {
    ...
    // This line imports the BrandDrop-Android to your project.
    implementation 'com.github.BoardActive:BD-SDK-Android:3.1.0'
    ...
}
```

## How to use the BrandDrop SDK

### Use BrandDrop SDK in your Launch Activity

```java
import com.boardactive.addrop.BoardActive;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    //Add the BrandDrop Object
    private BrandDrop mBrandDrop;
    Boolean isAllowImage = false;
    String imageUrl = "";
    HashMap<String,Object> updatedCustomAttributes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create an instant of BoardActive
        mBrandDrop = new BrandDrop(getApplicationContext());

        // Add URL to point to BoardActive REST API
        mBrandDrop.setAppUrl(BoardActive.APP_URL_PROD);

        // Add AppID provided by BoardActive
        mBrandDrop.setAppId("ADD_APP_ID");

        // Add AppKey provided by BoardActive
        mBrandDrop.setAppKey("ADD_APP_KEY");

        // Add the version of your App
        mBrandDrop.setAppVersion("1.0.0");


        // Get Firebase Token
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        String fcmToken = task.getResult().getToken();

                        // Add Firebase Token to BoardActive
                        mBrandDrop.setAppToken(fcmToken);

			//location permission
		        mBrandDrop.checkLocationPermissions();

                        // Initialize BoardActive
                        mBrandDrop.initialize();

                        mBrandDrop.setIsForeground(true);
                        mBrandDrop.StartWorker(getResources().getString(R.string.app_name)+" ");


                        // Register the device with BrandDrop

                        try {
                            mBrandDrop.registerDevice(new BrandDrop.PostRegisterCallback() {
                                @Override
                                public void onResponse(Object value) {
                                    Log.d(TAG, value.toString());
                                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setLenient().create();
                                    //   Me me = gson.fromJson(value.toString(), Me.class);
                                    try {
                                        JsonParser parser = new JsonParser();
                                        JsonElement je = parser.parse(value.toString());

                                        Log.d(TAG, gson.toJson(je));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
			// update custom attributes stuff

			updatedCustomAttributes = new HashMap<>();
			updatedCustomAttributes.put("braves_fan", true);
			mBrandDrop.putCustomAtrributes(new BoardActive.PutMeCallback() {
			    @Override
			    public void onResponse(Object value) {
			    }
			}, updatedCustomAttributes);
				    }
				});

        if (getIntent().getExtras() != null) {
            isAllowImage = getIntent().getBooleanExtra("isAllowImage", false);
            if (isAllowImage) {

                imageUrl = getIntent().getStringExtra("key.IMAGE_URL");
                new ImageAsync(this, imageUrl).execute();
                showAlert("Download Image", "Do you want to download this image?", this);

            } else {
                title = getIntent().getStringExtra("key.TITLE");
                desc = getIntent().getStringExtra("key.DESC");
                imageUrl = getIntent().getStringExtra("key.IMAGE_URL");

            }

        }
    }

    private void showAlert(String title, String message, Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            saveImage(bitmap, "downloadedImage", context);
                            dialog.dismiss();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        alertDialog.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

    public static void saveImage(Bitmap bitmap, @NonNull String name, Context context) throws IOException {
        boolean saved;
        OutputStream fos;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = context.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + "images");
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            fos = resolver.openOutputStream(imageUri);
        } else {
            String imagesDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).toString() + File.separator + "images";

            File file = new File(imagesDir);

            if (!file.exists()) {
                file.mkdir();
            }

            File image = new File(imagesDir, name + ".png");
            fos = new FileOutputStream(image);

        }
        saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
    }
}

class ImageAsync extends AsyncTask<String, Void, Bitmap> {

        Context mContext;
        String imageUrl;

        ImageAsync(Context context,String imageUrl)
        {
            this.imageUrl =imageUrl;
            this.mContext=context;
        }
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                MainActivity.bitmap = Glide.
                        with(mContext).
                        asBitmap().load(imageUrl).
                        into(300, 300). // Width and height
                                get();

            } catch (Exception e) {
                Log.d("TAG", e.toString());
            }
            return null;
        }

    }

```
```java
public class BrandDropApp extends Application {

    public static final String TAG = BrandDropApp.class.getName();
    public static final String CHANNEL_ID = "channel 1";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "[BrandDropApp] BrandDropApp onCreate()");
        createNotificationChannel();
        /**
         * This will start Firebase
         */
        FirebaseApp.initializeApp(this.getApplicationContext());
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Example Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }

    }
}
```



## Download Example App Source Code
There is an example app provided [here](https://github.com/BoardActive/BD-App-Android) for Android.

## Ask for Help

Our team wants to help. Please contact us 

* Help Documentation: [http://help.boardactive.com/en/](http://help.boardactive.com/en/)
* Call us: [(657)229-4669](tel:+6572294669)
* Email Us [taylor@boardactive.com](mailto:taylor@boardactive.com)
* Online Support [Web Site](https://www.boardactive.com/)
