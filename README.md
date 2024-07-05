# BrandDrop SDK Android

<img src="https://github.com/BoardActive/BD-SDK-Android/assets/108806504/32d7a6b0-afff-4bbc-ab9b-990c3fd27848?s=200&v=4" width="96" height="96"/>

### Location-Based Engagement
- Enhance your app. Empower your marketing.
    
- It's not about Advertising... It's about "PERSONALIZING"

- BrandDrop's platform connects brands to consumers using location-based engagement.

- Our international patent-pending Visualmatic™ software is a powerful marketing tool allowing brands to set up a virtual perimeter around any location, measure foot-traffic, and engage users with personalized messages when they enter geolocations

- Effectively attribute campaign efficiency by seeing where users go after the impression! 

- Use your BrandDrop account to create Places (geo-fenced areas) and Messages (notifications) to deliver custom messages to your app users.


[Click Here to get a BranDrop account](https://app.branddrop.us/login)

The BrandDrop SDK will use a device's location to know when an app user passes into a geo-fence. Passing into a geo-fence can trigger an event allowing you to deliver notifications to your app users.  

## Create a Firebase Project 
To use Firebase Cloud Messaging you must have a Firebase project. 

[Click Here to see the Firebase tutorial](https://firebase.google.com/docs/android/setup)

[Click Here to go to the Firebase Console](https://console.firebase.google.com/u/0/)

Once you create a related Firebase project you can download the google-service.json which you need to include with your android project.

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

### Include Location permissions to your Android Project
The BrandDrop SDK will check if permissions are allowed. If not, the SDK will automatically ask for location permissions.

## Add Firebase Messaging to your app

### Add Gradle Dependencies
If you app does not already support Firebase messaging you can follow these instructions to add to your app.

```javascript
...
dependencies {
    ...
    // This line imports the Firebase Support to your project.
    implementation  'com.google.firebase:firebase-core:21.1.1'
    implementation 'com.google.firebase:firebase-iid:21.1.0'
    implementation 'com.google.firebase:firebase-messaging:24.0.0'
    implementation 'androidx.work:work-runtime:2.9.0'
    ...
}
// Include Google Play Services
apply plugin: 'com.google.gms.google-services'

```
#### Notification Builder Class
```javascript
public class NotificationBuilder extends AsyncTask<String, Void, Bitmap> {
    private static final String TAG = "MyNotificationBuilder";
    private Bitmap mBitmap;

    private Context mContext;
    private MessageModel mObj;
    private PendingIntent mPendingIntent;
    private int mType;
    private Boolean isSilent;

    public static final String NOTIFICATION_KEY = "NOTIFICATION_KEY";
    public static final int NOTIFICATION_BASIC = 0;
    public static final int NOTIFICATION_BIG_PIC = 1;
    public static final int NOTIFICATION_ACTION_BUTTON = 2;
    public static final int NOTIFICATION_BIG_TEXT = 3;
    public static final int NOTIFICATION_INBOX = 4;

    public NotificationBuilder(Context context, PendingIntent pendingIntent, MessageModel obj, int type, Boolean isSilent) {
        super();
        this.mContext = context;
        this.mObj = obj;
        this.mPendingIntent = pendingIntent;
        this.mType = type;
        this.isSilent = isSilent;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            mBitmap = Glide.with(mContext).
                            asBitmap()
                            .load(mObj.getImageUrl())
                            .apply(new RequestOptions().override(300, 300))
                            .submit()
                            .get();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        String channelId = mContext.getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder;

        switch (mType) {
            case NOTIFICATION_BASIC:
                Log.e("Basic", "");//Basic Notification
                if (mObj.getAllowToDownloadNotificationImage()) {

                    notificationBuilder = new NotificationCompat.Builder(mContext, channelId)
                            .setContentTitle(mObj.getTitle())
                            .setContentText(mObj.getBody())
                            .setAutoCancel(true)
                            .setContentIntent(mPendingIntent)
                            .addAction(0, "Download Image", mPendingIntent)
                            .setSound(defaultSoundUri)
                            .setLargeIcon(mBitmap).setStyle(new NotificationCompat.BigPictureStyle()
                                    .bigPicture(mBitmap).bigLargeIcon((Bitmap) null))
                            .setSmallIcon(R.mipmap.ic_launcher_round)
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                } else {
                    notificationBuilder = new NotificationCompat.Builder(mContext, channelId)
                            .setContentTitle(mObj.getTitle())
                            .setContentText(mObj.getBody())
                            .setAutoCancel(true)
                            .setContentIntent(mPendingIntent)
                            .setSound(defaultSoundUri)
                            .setLargeIcon(mBitmap).setStyle(new NotificationCompat.BigPictureStyle()
                                    .bigPicture(mBitmap).bigLargeIcon((Bitmap) null))
                            .setSmallIcon(R.mipmap.ic_launcher_round)
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                }

                break;
            case NOTIFICATION_BIG_PIC:
                Log.e("Basic", "");//Big Pic Notification
                notificationBuilder =
                        new NotificationCompat.Builder(mContext, channelId)
                                .setContentTitle(mObj.getTitle())
                                .setContentText(mObj.getBody())
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .addAction(0, "Download Image", mPendingIntent)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setContentIntent(mPendingIntent)
                                .setLargeIcon(mBitmap)
                                .setSmallIcon(R.mipmap.ic_launcher_round)
                                .setStyle(new NotificationCompat.BigPictureStyle()
                                        .bigPicture(mBitmap)
                                        .bigLargeIcon(mBitmap));

                break;
            case NOTIFICATION_ACTION_BUTTON:
                Log.e("Action Button", "");//Action Button Notification
                notificationBuilder =
                        new NotificationCompat.Builder(mContext, channelId)
                                .setContentTitle(mObj.getTitle())
                                .setContentText(mObj.getBody())
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .addAction(0, "Download Image", mPendingIntent)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setContentIntent(mPendingIntent)
                                .setLargeIcon(mBitmap)
                                .setSmallIcon(R.mipmap.ic_launcher_round)
                                .addAction(0, "Action Button",
                                        mPendingIntent);
                ;
                break;
            case NOTIFICATION_BIG_TEXT:
                Log.e("Big Text", "");//Big Text Notification
                String bigText = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
                notificationBuilder =
                        new NotificationCompat.Builder(mContext, channelId)
                                .setContentTitle(mObj.getTitle())
                                .setContentText(mObj.getBody())
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .addAction(0, "Download Image", mPendingIntent)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setContentIntent(mPendingIntent)
                                .setLargeIcon(mBitmap)
                                .setSmallIcon(R.mipmap.ic_launcher_round)
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(bigText));
                break;
            case NOTIFICATION_INBOX: //Inbox Style Notification
                Log.e("Inbox", "");//Big Text Notification

                notificationBuilder =
                        new NotificationCompat.Builder(mContext, channelId)
                                .setContentTitle(mObj.getTitle())
                                .setContentText(mObj.getBody())
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .addAction(0, "Download Image", mPendingIntent)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setContentIntent(mPendingIntent)
                                .setLargeIcon(mBitmap)
                                .setSmallIcon(R.mipmap.ic_launcher_round)
                                .setStyle(new NotificationCompat.InboxStyle()
                                        .addLine("Sample MessageModel #1")
                                        .addLine("Sample MessageModel #2")
                                        .addLine("Sample MessageModel #3"))
                                .setContentIntent(mPendingIntent);
                break;
            default:
                Log.e("default", "");
                if (mObj.getAllowToDownloadNotificationImage()) {
                    notificationBuilder =
                            new NotificationCompat.Builder(mContext, channelId)
                                    .setContentTitle(mObj.getTitle())
                                    .setContentText(mObj.getBody())
                                    .setAutoCancel(true)
                                    .setLargeIcon(mBitmap)
                                    .addAction(0, "Download Image", mPendingIntent)
                                    .setSmallIcon(R.mipmap.ic_launcher_round).setStyle(new NotificationCompat.BigPictureStyle()
                                            .bigPicture(mBitmap).bigLargeIcon((Bitmap) null))
                                    .setSound(defaultSoundUri)
                                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                    .setContentIntent(mPendingIntent);
                } else {
                    notificationBuilder =
                            new NotificationCompat.Builder(mContext, channelId)
                                    .setContentTitle(mObj.getTitle())
                                    .setContentText(mObj.getBody())
                                    .setAutoCancel(true)
                                    .setLargeIcon(mBitmap)
                                    .setSmallIcon(R.mipmap.ic_launcher_round).setStyle(new NotificationCompat.BigPictureStyle()
                                            .bigPicture(mBitmap).bigLargeIcon((Bitmap) null))
                                    .setSound(defaultSoundUri)
                                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                    .setContentIntent(mPendingIntent);
                }

                break;
        }

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);

        }
        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        notificationManager.notify(m, notificationBuilder.build());
    }
}
```

#### Messaging Service Class
```javascript
public class FCMService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";
    public static final String EXTRA_OBJECT = "key.EXTRA_OBJECT";
    public static final String EXTRA_MESSADE_ID = "key.EXTRA_MESSADE_ID";
    private static final String MESSAGE_ID = "MESSAGE_ID";
    public static final String IMAGE_URL = "key.IMAGE_URL";
    public static final String TITLE = "key.TITLE";
    public static final String DESC = "key.DESC";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "on message received data");
        Log.d(TAG, "[BAAdDrop] From: " + remoteMessage.getFrom());
        Log.d(TAG, "notification data");

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "[BAAdDrop] MessageModel data payload: " + remoteMessage.getData());
            sendNotification(remoteMessage);

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "[BAAdDrop] MessageModel Notification Body: " + remoteMessage);
        }
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "[BAAdDrop] Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    private void scheduleJob() {
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(FCMWorker.class)
                .build();
        WorkManager.getInstance().beginWith(work).enqueue();
    }

    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     * Set notification's tap action.
     *
     * @param remoteMessage FCM notification.
     */
    private void sendNotification(final RemoteMessage remoteMessage) {
        Long currentDateandTime = System.currentTimeMillis();
        MessageModel obj = new MessageModel();
        Log.e("firebase message id",""+remoteMessage.getMessageId().trim());

        Log.e("firebase message", Objects.requireNonNull(remoteMessage.getMessageId().trim()));

        int id = 1;
        obj.setId(id);
        obj.setBaMessageId(remoteMessage.getData().get("baMessageId"));
        obj.setBaNotificationId(remoteMessage.getData().get("baNotificationId"));
        obj.setFirebaseNotificationId(remoteMessage.getMessageId());
        obj.setTitle(remoteMessage.getData().get("title"));
        obj.setBody(remoteMessage.getData().get("body"));
        obj.setImageUrl(remoteMessage.getData().get("imageUrl"));
        obj.setLatitude(remoteMessage.getData().get("latitude"));
        obj.setLongitude(remoteMessage.getData().get("longitude"));
        obj.setMessageData(remoteMessage.getData().get("messageData"));
        obj.setIsTestMessage(remoteMessage.getData().get("isTestMessage"));
        obj.setDateCreated(currentDateandTime);
        obj.setDateLastUpdated(currentDateandTime);
        Boolean isSilent = Boolean.valueOf(remoteMessage.getData().get("isSilent"));
        String action = remoteMessage.getData().get("action");
        String type = remoteMessage.getData().get("type");
        String placeId = remoteMessage.getData().get("placeId");
        Boolean isAllowToDownloadNotificationImage = Boolean.valueOf(remoteMessage.getData().get("isAllowToDownloadNotificationImage"));
        obj.setAllowToDownloadNotificationImage(isAllowToDownloadNotificationImage);
        Log.v(isSilent.toString(),"");
        Log.v(action,"action");
        obj.setAction(action);


        BrandDrop mBrandDrop = new BrandDrop(getApplicationContext());
        mBrandDrop.postEvent(new BrandDrop.PostEventCallback() {
            @Override
            public void onResponse(Object value) {
                Log.d(TAG, "[BrandDropApp] Received Event: " + value.toString());
            }
        }, "received", obj.getBaMessageId(), obj.getBaNotificationId(), obj.getFirebaseNotificationId().trim());

        if(type != null)
        {
            if(type.equals("app_status") && Objects.equals(placeId, "null"))
        {
            if(action != null){
                if(action.equals("Enable")){
                    SharedPreferenceHelper.putString(this, Constants.APP_STATUS, action);
                }
                else
                {
                    SharedPreferenceHelper.putString(this, Constants.APP_STATUS, action);
                }
            }
        }else if(type.equals("place_update") || type.equals("Campaign") || Objects.equals(placeId, "null")){
           // SharedPreferenceHelper.putString(this, Constants.APP_STATUS, action);
            mBrandDrop.setLocationArrayList(null);
            mBrandDrop.getLocationList();
        }
        }
        if(!isSilent){
            Intent intent = new Intent(this, DisplayImageActivity.class);
            intent.putExtra(EXTRA_MESSADE_ID, id);
            intent.putExtra(TITLE,obj.getTitle());
            intent.putExtra(DESC,obj.getBody());
            intent.putExtra("MessageId",obj.getBaMessageId());
            intent.putExtra("NotificationId",obj.getBaNotificationId());
            intent.putExtra("FirebaseMessageId",obj.getFirebaseNotificationId());
            if(obj.getImageUrl() != null){
                intent.putExtra(IMAGE_URL,obj.getImageUrl());

            }
            if(obj.getAllowToDownloadNotificationImage()){
                intent.putExtra("isAllowImage",true);

            }
            else
            {
                intent.putExtra("isAllowImage",false);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NO_HISTORY
            );
            intent.setAction(Intent.ACTION_VIEW);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(intent);
            PendingIntent pendingIntent =null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                int requestCode = new Random().nextInt();
                pendingIntent = PendingIntent.getActivity(
                        this, requestCode, intent,
                         PendingIntent.FLAG_MUTABLE);

                int notificationType = 0;
                new NotificationBuilder(this,pendingIntent, obj, notificationType,isSilent).execute();
            }else
            {
                int requestCode = new Random().nextInt();
                pendingIntent = PendingIntent.getActivity(
                        this, requestCode, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                int notificationType = 0;
                new NotificationBuilder(this,pendingIntent, obj, notificationType,isSilent).execute();
            }
        }
    }
}
```

#### FCM Worker Class

```java
public class FCMWorker extends Worker {

    public static final String TAG = FCMWorker.class.getName();

    public FCMWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "[BDAdDrop] Performing long running task in scheduled job");
        return Result.success();
    }
}
```
#### Message Model Class

```java
public class MessageModel implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("baMessageId")
    @Expose
    private String baMessageId;

    @SerializedName("baNotificationId")
    @Expose
    private String baNotificationId;

    @SerializedName("firebaseNotificationId")
    @Expose
    private String firebaseNotificationId;

    @SerializedName("body")
    @Expose
    private String body;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;

    @SerializedName("latitude")
    @Expose
    private String latitude;

    @SerializedName("longitude")
    @Expose
    private String longitude;

    @SerializedName("messageData")
    @Expose
    private String messageData;

    @SerializedName("isTestMessage")
    @Expose
    private String isTestMessage;

    @SerializedName("isRead")
    @Expose
    private Boolean isRead;

    @SerializedName("dateCreated")
    @Expose
    private Long dateCreated;

    @SerializedName("dateLastUpdated")
    @Expose
    private Long dateLastUpdated;

    @SerializedName("action")
    @Expose
    private String action;

    @SerializedName("isAllowToDownloadNotificationImage")
    @Expose
    private Boolean isAllowToDownloadNotificationImage;

    public MessageModel() {
    }

    public MessageModel(
            Integer id,
            String baMessageId,
            String baNotificationId,
            String firebaseNotificationId,
            String title,
            String body,
            String imageUrl,
            String latitude,
            String longitude,
            String messageData,
            String isTestMessage,
            Boolean isRead,
            Long dateCreated,
            Long dateLastUpdated,
            String action
    ) {
        super();
        this.id = id;
        this.baMessageId = baMessageId;
        this.baNotificationId = baNotificationId;
        this.firebaseNotificationId = firebaseNotificationId;
        this.title = title;
        this.body = body;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.messageData = messageData;
        this.isTestMessage = isTestMessage;
        this.isRead = isRead;
        this.dateCreated = dateCreated;
        this.dateLastUpdated = dateLastUpdated;
        this.action =action;
    }

    protected MessageModel(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        baMessageId = in.readString();
        baNotificationId = in.readString();
        firebaseNotificationId = in.readString();
        body = in.readString();
        title = in.readString();
        imageUrl = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        messageData = in.readString();
        isTestMessage = in.readString();
        byte tmpIsRead = in.readByte();
        isRead = tmpIsRead == 0 ? null : tmpIsRead == 1;
        if (in.readByte() == 0) {
            dateCreated = null;
        } else {
            dateCreated = in.readLong();
        }
        if (in.readByte() == 0) {
            dateLastUpdated = null;
        } else {
            dateLastUpdated = in.readLong();
        }
        action = in.readString();
        byte tmpIsAllowToDownloadNotificationImage = in.readByte();
        isAllowToDownloadNotificationImage = tmpIsAllowToDownloadNotificationImage == 0 ? null : tmpIsAllowToDownloadNotificationImage == 1;
    }

    public static final Creator<MessageModel> CREATOR = new Creator<MessageModel>() {
        @Override
        public MessageModel createFromParcel(Parcel in) {
            return new MessageModel(in);
        }

        @Override
        public MessageModel[] newArray(int size) {
            return new MessageModel[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBaMessageId() {
        return baMessageId;
    }

    public void setBaMessageId(String messageId) {
        this.baMessageId = messageId;
    }

    public String getBaNotificationId() {
        return baNotificationId;
    }

    public void setBaNotificationId(String baNotificationId) {
        this.baNotificationId = baNotificationId;
    }

    public String getFirebaseNotificationId() {
        return firebaseNotificationId;
    }

    public void setFirebaseNotificationId(String firebaseNotificationId) {
        this.firebaseNotificationId = firebaseNotificationId;
    }

    public Boolean getAllowToDownloadNotificationImage() {
        return isAllowToDownloadNotificationImage;
    }

    public void setAllowToDownloadNotificationImage(Boolean allowToDownloadNotificationImage) {
        isAllowToDownloadNotificationImage = allowToDownloadNotificationImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getMessageData() {
        return messageData;
    }

    public void setMessageData(String messageData) {
        this.messageData = messageData;
    }

    public String getIsTestMessage() {
        return isTestMessage;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsTestMessage(String isTestMessage) {
        this.isTestMessage = isTestMessage;
    }

    public Long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Long getDateLastUpdated() {
        return dateLastUpdated;
    }

    public void setDateLastUpdated(Long dateLastUpdated) {
        this.dateLastUpdated = dateLastUpdated;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(id);
        }
        parcel.writeString(baMessageId);
        parcel.writeString(baNotificationId);
        parcel.writeString(firebaseNotificationId);
        parcel.writeString(body);
        parcel.writeString(title);
        parcel.writeString(imageUrl);
        parcel.writeString(latitude);
        parcel.writeString(longitude);
        parcel.writeString(messageData);
        parcel.writeString(isTestMessage);
        parcel.writeByte((byte) (isRead == null ? 0 : isRead ? 1 : 2));
        if (dateCreated == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(dateCreated);
        }
        if (dateLastUpdated == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(dateLastUpdated);
        }
        parcel.writeString(action);
        parcel.writeByte((byte) (isAllowToDownloadNotificationImage == null ? 0 : isAllowToDownloadNotificationImage ? 1 : 2));
    }
}
```

#### Add to your AndroidManifest.xml

```xml

	<uses-permission android:name="android.permission.INTERNET" />

	<application
	   ...
	   android:name="BrandDropApp">

	<meta-data
            android:name="com.google.firebase.messaging.default_notification_icon"
            android:resource="@drawable/ic_notification" />
        <meta-data
            android:name="com.google.firebase.messaging.default_notification_color"
            android:resource="@color/colorAccent" />
        <meta-data
            android:name="com.google.firebase.messaging.default_notification_channel_id"
            android:value="@string/default_notification_channel_id" />

        <receiver
            android:name="com.branddrop.bakit.GeofenceBroadCastReceiver"
            android:allowBackup="true"
            android:enabled="true"
            android:exported="true" />

        <service
            android:name="com.branddrop.bakit.LocationUpdatesService"
            android:enabled="true"
            android:exported="false"
            android:foregroundServiceType="location" />

     	<service
          android:name="com.branddrop.addrop.firebase.FCMService"
          android:exported="false">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT" />
            </intent-filter>
     	</service>
```

## How to use the BrandDrop SDK

### Use BrandDrop SDK in your Launch Activity

```java
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private BrandDrop mBrandDrop;
    Boolean isAllowImage = false;
    String imageUrl = "";
    HashMap<String,Object> updatedCustomAttributes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBrandDrop = new BrandDrop(getApplicationContext());

        mBrandDrop.setAppUrl(BrandDrop.APP_URL_PROD);

        // Add AppID provided by BrandDrop
        mBrandDrop.setAppId("ADD_APP_ID");

        // Add AppKey provided by BrandDrop
        mBrandDrop.setAppKey("ADD_APP_KEY");

        mBrandDrop.setAppVersion("1.0.0");

        // Get Firebase Token
	FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w(TAG, "getInstanceId failed", task.getException());
                return;
            }

            String fcmToken = task.getResult();

            // Add Firebase Token to BoardActive
            mBrandDrop.setAppToken(fcmToken);

            //location permission
            mBrandDrop.checkLocationPermissions();

            // Initialize BoardActive
            mBrandDrop.initialize();

            mBrandDrop.setIsForeground(true);
            mBrandDrop.StartWorker(getResources().getString(R.string.app_name) + " ");

            // Register the device with BrandDrop
            try {
                mBrandDrop.registerDevice(new BrandDrop.PostRegisterCallback() {
                    @Override
                    public void onResponse(Object value) {
                        Log.d(TAG, value.toString());
                        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setLenient().create();
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
            mBrandDrop.putCustomAtrributes(new BrandDrop.PutMeCallback() {
                @Override
                public void onResponse(Object value) {
                }
            }, updatedCustomAttributes);
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
                MainActivity.bitmap = Glide.with(mContext)
				.asBitmap().load(imageUrl)
				.into(300, 300).get();
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
         * Firebase Initialization
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
