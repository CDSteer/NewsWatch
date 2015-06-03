package com.example.sonymobile.smartextension.hellonotification;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
    StringBuilder builder = new StringBuilder();
    HttpClient client = new DefaultHttpClient();
    android.os.Handler mHandler;
    public static ArrayList<Article> articles = new ArrayList<Article>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //
        return Service.START_STICKY;
    }

    @Override
    public void onCreate(){
        mHandler = new android.os.Handler();
        ping();
        Toast toast = Toast.makeText(getApplicationContext(), "NewsReadService Started", Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void ping() {
        try {
            String newsJson = readNews();
            parseNews(newsJson);
        } catch (Exception e) {
            Log.e("Error", "In onStartCommand");
            e.printStackTrace();
        }
        scheduleNext();
    }

    private void parseNews(String json) {
        JSONArray jsonArray = null;
        String[] news = new String[5];
        try {
            JSONObject jsonObject = new JSONObject(json);
            jsonArray = jsonObject.getJSONArray("articles");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jo = jsonArray.getJSONObject(i);
                news[0] = jo.getString("title");
                news[1] = jo.getString("description");
                news[2] = jo.getString("cps_id");
                JSONObject imageObject = jo.getJSONObject("image");
                news[3] = imageObject.getString("src");
                news[4] = jo.getString("url");
                addToArticles(news);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addToArticles(String[] news) {
        articles.add(new Article(news[0], news[1], news[2], news[3], news[4]));
    }

    private void scheduleNext() {
        mHandler.postDelayed(new Runnable() {
            public void run() {
                ping();
            }
        }, 60000);
    }

    public String readNews() {
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

    public static ArrayList<Article> getArticles() {
        return articles;
    }
}