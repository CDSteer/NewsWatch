package com.example.sonymobile.smartextension.hellonotification;

import android.app.Activity;
import android.app.LauncherActivity;
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
import java.util.List;

/**
 * Created by cdsteer on 29/05/15.
 */
public class ViewNews extends Activity {
    final String KEYWORDS = "London";
    final String PRODUCT = "NewsWeb";
    final String CONTENT_FORMAT = "TextualFormat";
    final String RECENT_FIRST = "yes";
    final String URL = "http://data.bbc.co.uk/bbcrd-juicer/articles.json?text=" + KEYWORDS + "&product[]="
            + PRODUCT + "&content_format[]=" + CONTENT_FORMAT + "&recent_first=" +
            RECENT_FIRST + "&apikey=3O320TNQSzygKXF8frRiNBQnAANSyUl7";

    Cursor cursor;
    protected ListAdapter adapter;
    protected ListView memoList;
    protected int[] listFields = new int[] {R.id.name, R.id.details};
    protected String[] fields = new String[] {"_id","title", "description"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_list);
        //memoList = (ListView) findViewById (R.id.list);
//        memoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//        });
        listMemos();
    }



    public void listMemos() {
        String news = readNews();
        addData(news);

    }

    public void addData(String json){
        JSONArray jsonArray = null;
        String[] news = new String[2];
        try {
            JSONObject jsonObject = new JSONObject(json);
            jsonArray = jsonObject.getJSONArray("articles");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jo = null;
            try {
                jo = jsonArray.getJSONObject(i);
                news[0] = jo.getString("title");
                news[1] = jo.getString("description");
                addList(news);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addList(String[] news) {
        ListView listView = (ListView) findViewById(R.id.news_list);

        TextView textView = new TextView(this.getApplicationContext());
        textView.setText(news[0]);
        textView.setTextColor(Color.BLACK);
        listView.addHeaderView(textView);


        ArrayList arrayList = new ArrayList<String>();
        //arrayList.add(news[0]);

        // Adapter: You need three parameters 'the context, id of the layout (it will be where the data is shown),
        // and the array that contains the data
        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.news_list_item, arrayList);

        // Here, you set the data in your ListView
        listView.setAdapter(adapter);

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
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject mJsonObject = jsonArray.getJSONObject(i);
                news[0] = mJsonObject.getString("title");
                news[1] = mJsonObject.getString("description");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        listMemos();
    }

}
