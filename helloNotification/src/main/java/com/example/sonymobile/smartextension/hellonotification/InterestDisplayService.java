package com.example.sonymobile.smartextension.hellonotification;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.sonyericsson.extras.liveware.aef.notification.Notification;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.notification.NotificationUtil;

import java.util.Random;

/**
 * Created by cdsteer on 29/05/15.
 */
public class InterestDisplayService extends Service {
    android.os.Handler mHandler;
    Random rand = new Random();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler = new android.os.Handler();
        ping();
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void ping() {
        try {
            int x = rand.nextInt(ViewNews.savedNews.size()-1);
            String[] fields = new String[] {"BBC News", ViewNews.savedNews.get(x)};
            sendNews(fields);
        } catch (Exception e) {
            Log.e("Error", "In onStartCommand");
            e.printStackTrace();
        }
        scheduleNext();
    }

    private void scheduleNext() {
        mHandler.postDelayed(new Runnable() {
            public void run() { ping(); }
        }, 60000);
    }

    private void sendNews(String[] news){
        long time = System.currentTimeMillis();
        long sourceId = NotificationUtil.getSourceId(this,
                HelloNotificationExtensionService.EXTENSION_SPECIFIC_ID);
        if (sourceId == NotificationUtil.INVALID_ID) {
            Log.e(HelloNotificationExtensionService.LOG_TAG, "Failed to insert data");
            return;
        }
        String profileImage = ExtensionUtils.getUriString(this,
                R.drawable.widget_default_userpic_bg);
        ContentValues eventValues = new ContentValues();
        eventValues.put(Notification.EventColumns.EVENT_READ_STATUS, false);
        eventValues.put(Notification.EventColumns.DISPLAY_NAME, news[0]);
        eventValues.put(Notification.EventColumns.MESSAGE, news[1]);
        eventValues.put(Notification.EventColumns.PERSONAL, 1);
        eventValues.put(Notification.EventColumns.PROFILE_IMAGE_URI, profileImage);
        eventValues.put(Notification.EventColumns.PUBLISHED_TIME, time);
        eventValues.put(Notification.EventColumns.SOURCE_ID, sourceId);
        NotificationUtil.addEvent(this, eventValues);
    }
}
