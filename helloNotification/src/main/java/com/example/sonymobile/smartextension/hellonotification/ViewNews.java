package com.example.sonymobile.smartextension.hellonotification;

import android.app.Activity;
import android.app.LauncherActivity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
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
import java.util.Arrays;
import java.util.List;

/**
 * Created by cdsteer on 29/05/15.
 */
public class ViewNews extends Activity {

    protected ListAdapter adapter;

    ListView listView;
    ArrayList<Article> articles = new ArrayList();
    //String newsJson = NewsReadService.newsJson;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_list);
        listView = (ListView) findViewById (R.id.news_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.name);
                String text = (String) textView.getText();
                Article article = (Article)textView.getTag();
                article.changeInterest();
                if (article.isInterested() == true) {
                    view.setBackgroundColor(Color.parseColor("#00b200"));
                } else {
                    view.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
            }
        });
        populateList();
    }

    private void populateList() {
        adapter = new MyArrayAdapter(this, NewsReadService.getArticles());
        listView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        populateList();
    }
}