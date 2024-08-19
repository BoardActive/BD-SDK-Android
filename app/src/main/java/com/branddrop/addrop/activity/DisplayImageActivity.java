package com.branddrop.addrop.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.branddrop.addrop.R;
import com.branddrop.addrop.utils.ImageUtils;
import com.branddrop.bakit.BrandDrop;
import com.branddrop.bakit.models.MessageModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;

public class DisplayImageActivity extends AppCompatActivity {
    TextView textTitle;
    TextView textDesc;
    ImageView imageNotification;
    ImageView closeImage;
    String messageId;
    String notificationId;
    String firebaseNotificationId;
    public  static Bitmap bitmap;
    MessageModel messageModel = new MessageModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        init();
    }

    public  void init(){
        textDesc = findViewById(R.id.txtDesc);
        textTitle = findViewById(R.id.txtTitle);
        imageNotification = findViewById(R.id.notificationImage);
        closeImage = findViewById(R.id.closeImage);
        String title="";
        String desc="";
        Boolean isAllowImage=false;
        String imageUrl ="";
        if(getIntent().getExtras() != null){
            isAllowImage = getIntent().getBooleanExtra("isAllowImage",false);

            if(isAllowImage)
            {
                title= getIntent().getStringExtra("key.TITLE");
                desc= getIntent().getStringExtra("key.DESC");
                imageUrl= getIntent().getStringExtra("key.IMAGE_URL");
                textTitle.setText(title);
                textDesc.setText(desc);
                new NotificationBuilder(this,imageUrl).execute();
                showAlert("Download Image","Do you want to download this image?",this);
                if(imageUrl != null){
                    try {
                        Glide.with(this).load(Uri.parse(imageUrl)).into(imageNotification);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

            }else
            {
                title= getIntent().getStringExtra("key.TITLE");
                desc= getIntent().getStringExtra("key.DESC");
                imageUrl= getIntent().getStringExtra("key.IMAGE_URL");
                messageId = getIntent().getStringExtra("MessageId");
                firebaseNotificationId = getIntent().getStringExtra("FirebaseMessageId").trim();
                notificationId = getIntent().getStringExtra("NotificationId");
                textTitle.setText(title);
                textDesc.setText(desc);
                if(imageUrl != null){
                    try {
                        Glide.with(this).load(Uri.parse(imageUrl)).into(imageNotification);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }



        }
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

        BrandDrop mBrandDrop = new BrandDrop(getApplicationContext());

        mBrandDrop.postEvent(new BrandDrop.PostEventCallback() {

            @Override
            public void onResponse(Object value) {
                Log.d("TAG", "[BrandDropApp] Received Event: " + value.toString());
            }
        }, "opened",messageId, notificationId, firebaseNotificationId);
    }

    private void showAlert(String title, String message, Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            ImageUtils.saveImage(bitmap,"downloadedImage",context);
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

}
class NotificationBuilder extends AsyncTask<String, Void, Bitmap>{

    Context mContext;
    String imageUrl;

    NotificationBuilder(Context context,String imageUrl)
    {
        this.imageUrl =imageUrl;
        this.mContext=context;
    }
    @Override
    protected Bitmap doInBackground(String... strings) {
        try {
            DisplayImageActivity.bitmap =
//                    Glide.
//                    with(mContext).
//                    load(imageUrl).
//                    asBitmap().
//                    into(300, 300). // Width and height
//                            get();
            Glide.
                    with(mContext).
                    asBitmap()
                    .load(imageUrl)
                    .apply(new RequestOptions()
                            .override(300, 300))
                    .submit()
                    .get();
        } catch (Exception e) {
            Log.d("TAG", e.toString());
        }
        return null;
    }

}