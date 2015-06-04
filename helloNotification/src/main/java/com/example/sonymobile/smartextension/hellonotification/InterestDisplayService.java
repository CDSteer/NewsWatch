package com.example.sonymobile.smartextension.hellonotification;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.aef.notification.Notification;
import com.sonyericsson.extras.liveware.aef.widget.Widget;
import com.sonyericsson.extras.liveware.extension.util.ExtensionService;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.notification.NotificationUtil;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by cdsteer on 29/05/15.
 */
public class InterestDisplayService extends ExtensionService {
    android.os.Handler mHandler;
    Random rand = new Random();

    public static ArrayList<String> savedNews = new ArrayList<String>();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    protected RegistrationInformation getRegistrationInformation() {
        return null;
    }

    @Override
    protected boolean keepRunningWhenConnected() {
        return true;
    }

    @Override
    public void onCreate(){
        mHandler = new android.os.Handler();
        ping();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void ping() {
        try {
            ArrayList<Article> articles = NewsReadService.getArticles();
            for (int i = 0; i < articles.size(); i++) {
                if (articles.get(i).isInterested() == true) {
                    sendNews(articles.get(i));
                }
            }
        } catch (Exception e) {
            Log.e("Error", "In onStartCommand");
            e.printStackTrace();
        }
        scheduleNext();
    }

    private void scheduleNext() {
        mHandler.postDelayed(new Runnable() {
            public void run() {
                ping();
            }
        }, 30000);
    }

    private void sendNews(Article news){
        long time = System.currentTimeMillis();
        long sourceId = NotificationUtil.getSourceId(this,
                HelloNotificationExtensionService.EXTENSION_SPECIFIC_ID);
        if (sourceId == NotificationUtil.INVALID_ID) {
            Log.e(HelloNotificationExtensionService.LOG_TAG, "Failed to insert data");
            return;
        }
        Drawable d = new BitmapDrawable(getResources(), news.getImage());
        String profileImage = ExtensionUtils.getUriString(this,
                R.drawable.widget_default_userpic_bg);
        String imageURI = "";
        try {
            String path = Environment.getExternalStorageDirectory().toString();
            File file = new File(path, news.getcpsID() + ".jpg");
            FileOutputStream out = new FileOutputStream(file);
            news.getImage().compress(Bitmap.CompressFormat.PNG, 100, out);
            imageURI = file.toURI().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }



        //Log.v("InterestsImage", profileImage);
        ContentValues eventValues = new ContentValues();
        eventValues.put(Notification.EventColumns.EVENT_READ_STATUS, false);
        eventValues.put(Notification.EventColumns.DISPLAY_NAME, news.getTitle());
        eventValues.put(Notification.EventColumns.MESSAGE, news.getDescription());
        eventValues.put(Notification.EventColumns.PERSONAL, 1);
        eventValues.put(Notification.EventColumns.PROFILE_IMAGE_URI, imageURI);
        eventValues.put(Notification.EventColumns.PUBLISHED_TIME, time);
        eventValues.put(Notification.EventColumns.SOURCE_ID, sourceId);
        eventValues.put(Notification.EventColumns.IMAGE_URI, ExtensionUtils.getUriString(this.getApplicationContext(),R.drawable.blue_notification));
        Log.v("NewImage", "" + R.drawable.blue_notification);
        NotificationUtil.addEvent(this, eventValues);
    }
}
