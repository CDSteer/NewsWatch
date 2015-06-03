package com.example.sonymobile.smartextension.hellonotification;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.util.Log;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.aef.notification.Notification;
import com.sonyericsson.extras.liveware.aef.registration.Registration;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.notification.NotificationUtil;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by cdsteer on 29/05/15.
 */
public class InterestDisplayService extends Service {
    android.os.Handler mHandler;
    Random rand = new Random();

    public static ArrayList<String> savedNews = new ArrayList<String>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //
        return Service.START_STICKY;
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
            public void run() { ping(); }
        }, 10000);
    }

    private void sendNews(Article news){
        MyControlExtension myControlExtension = new MyControlExtension(this.getApplicationContext(), this.getPackageName());
        myControlExtension.setScreenState(Control.Intents.SCREEN_STATE_DIM); // (to make sure the screen is on)
        myControlExtension.showLayout(R.layout.smart_watch_widget, null);
        // --
        // (render your image here, to a bitmap that you keep in memory (for speed))
        // ---


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
        myControlExtension.sendImage(R.id.smart_watch_notification_widget_background, news.getImage());
        
        Log.v("InterestsImage",profileImage);
        ContentValues eventValues = new ContentValues();
        eventValues.put(Notification.EventColumns.EVENT_READ_STATUS, false);
        eventValues.put(Notification.EventColumns.DISPLAY_NAME, news.getTitle());
        eventValues.put(Notification.EventColumns.MESSAGE, news.getDescription());
        eventValues.put(Notification.EventColumns.PERSONAL, 1);
        eventValues.put(Notification.EventColumns.PROFILE_IMAGE_URI, profileImage);
        eventValues.put(Notification.EventColumns.PUBLISHED_TIME, time);
        eventValues.put(Notification.EventColumns.SOURCE_ID, sourceId);
        NotificationUtil.addEvent(this, eventValues);
    }
}
