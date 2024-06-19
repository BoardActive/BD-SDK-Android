/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.branddrop.addrop.firebase;

import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.TaskStackBuilder;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.branddrop.addrop.activity.DisplayImageActivity;
import com.branddrop.bakit.BrandDrop;
import com.branddrop.bakit.Tools.SharedPreferenceHelper;
import com.branddrop.bakit.models.MessageModel;
import com.branddrop.bakit.utils.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;
import java.util.Random;

/**
 * NOTE: There can only be one service in each app that receives FCM messages. If multiple
 * are declared in the Manifest then the first one will be chosen.
 *
 * In order to make this Java sample functional, you must remove the following from the Kotlin messaging
 * service in the AndroidManifest.xml:
 *
 * <intent-filter>
 *   <action android:name="com.google.firebase.MESSAGING_EVENT" />
 * </intent-filter>
 */
public class FCMService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";
    public static final String EXTRA_OBJECT = "key.EXTRA_OBJECT";
    public static final String EXTRA_MESSADE_ID = "key.EXTRA_MESSADE_ID";
    private static final String MESSAGE_ID = "MESSAGE_ID";
    public static final String IMAGE_URL = "key.IMAGE_URL";
    public static final String TITLE = "key.TITLE";
    public static final String DESC = "key.DESC";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]


        Log.d(TAG, "on message received data");

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
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

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "[BAAdDrop] Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(FCMWorker.class)
                .build();
        WorkManager.getInstance().beginWith(work).enqueue();
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
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

            }else
            {
                intent.putExtra("isAllowImage",false);

            }
//        intent.putExtra(EXTRA_OBJECT, obj);
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
               //  pendingIntent = stackBuilder.getPendingIntent(id, PendingIntent.FLAG_MUTABLE);
                int requestCode = new Random().nextInt();
                pendingIntent = PendingIntent.getActivity(
                        this, requestCode, intent,
                         PendingIntent.FLAG_MUTABLE);


//        int notificationType = Tools.getSharedPrecerenceInt(this, NotificationBuilder.NOTIFICATION_KEY);
                int notificationType = 0;
                new com.branddrop.addrop.firebase.NotificationBuilder(this,pendingIntent, obj, notificationType,isSilent).execute();
            }else
            {
                 //pendingIntent = stackBuilder.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);
                int requestCode = new Random().nextInt();
                pendingIntent = PendingIntent.getActivity(
                        this, requestCode, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

//        int notificationType = Tools.getSharedPrecerenceInt(this, NotificationBuilder.NOTIFICATION_KEY);
                int notificationType = 0;
                new com.branddrop.addrop.firebase.NotificationBuilder(this,pendingIntent, obj, notificationType,isSilent).execute();
            }
        }


    }

}