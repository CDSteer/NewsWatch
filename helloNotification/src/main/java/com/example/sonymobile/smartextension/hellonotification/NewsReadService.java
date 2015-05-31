package com.example.sonymobile.smartextension.hellonotification;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by cdsteer on 31/05/15.
 */
public class NewsReadService extends Service{
    final String KEYWORDS = "Swansea";
    final String PRODUCT = "NewsWeb";
    final String CONTENT_FORMAT = "TextualFormat";
    final String RECENT_FIRST = "yes";
    final String URL = "http://data.bbc.co.uk/bbcrd-juicer/articles.json?text=" + KEYWORDS + "&product[]="
            + PRODUCT + "&content_format[]=" + CONTENT_FORMAT + "&recent_first=" +
            RECENT_FIRST + "&apikey=3O320TNQSzygKXF8frRiNBQnAANSyUl7";

    android.os.Handler mHandler;
    public static String newsJson;

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
            readNews();
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
        }, 60000);
    }

    public void readNews() {
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
        newsJson = builder.toString();
    }
}