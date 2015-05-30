package com.example.sonymobile.smartextension.hellonotification;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.sonyericsson.extras.liveware.aef.notification.Notification;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.notification.NotificationUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.logging.Handler;

/**
 * Created by cdsteer on 29/05/15.
 */
public class NewsService extends Service {
    android.os.Handler mHandler;
    final String KEYWORDS = "London";
    final String PRODUCT = "NewsWeb";
    final String CONTENT_FORMAT = "TextualFormat";
    final String RECENT_FIRST = "yes";
    final String URL = "http://data.bbc.co.uk/bbcrd-juicer/articles.json?text=" + KEYWORDS + "&product[]="
            + PRODUCT + "&content_format[]=" + CONTENT_FORMAT + "&recent_first=" +
            RECENT_FIRST + "&apikey=3O320TNQSzygKXF8frRiNBQnAANSyUl7";
    Random rand = new Random();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler = new android.os.Handler();
        ping();
        return Service.START_STICKY;
    }
    private void ping() {
        try {
            //Your code here or call a method
            //String theNews = readNews();
            //parseNews(theNews);
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


    @Override
    public IBinder onBind(Intent intent) {
        return null;
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

        // Build the notification.
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

    public String readNews() {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        String line = "";
        HttpGet httpGet = new HttpGet(URL);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e("Getting JSON:", "Failed to download file");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    private void parseNews(String result) {
        String[] news = new String[2];
        try {
            JSONObject jObj = new JSONObject(result);
            JSONArray jsonArray = jObj.getJSONArray("articles");
            int x = rand.nextInt(jsonArray.length()-1);
            JSONObject mJsonObject = jsonArray.getJSONObject(x);
            news[0] = mJsonObject.getString("title");
            news[1] = mJsonObject.getString("description");
            sendNews(news);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
