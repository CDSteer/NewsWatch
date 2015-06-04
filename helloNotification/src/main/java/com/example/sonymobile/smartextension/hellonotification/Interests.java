package com.example.sonymobile.smartextension.hellonotification;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sonyericsson.extras.liveware.aef.control.Control;

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


public class Interests extends Activity {


    final String KEYWORDS = "";
    final String PRODUCT = "NewsWeb";
    final String CONTENT_FORMAT = "TextualFormat";
    final String RECENT_FIRST = "yes";
    final String PUBLISHED_AFTER = "2015-06-03T00:00:00.000Z";
    final String URL = "http://data.test.bbc.co.uk/bbcrd-juicer/articles?text=" + KEYWORDS + "&product[]="
            + PRODUCT + "&content_format[]=" + CONTENT_FORMAT + "&published_after=" + PUBLISHED_AFTER + "&recent_first=" +
            RECENT_FIRST + "&apikey=3O320TNQSzygKXF8frRiNBQnAANSyUl7";
    StringBuilder builder = new StringBuilder();
    HttpClient client = new DefaultHttpClient();

    protected ListAdapter adapter;
    ListView listView;
    static ArrayList<Article> keywords = new ArrayList<Article>();


    private void populateList() {
        adapter = new MyArrayAdapter(this, keywords);
        listView.setAdapter(adapter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_list);

        String news = readNews();
        parseNews(news);

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


//                TextView textView = (TextView) view.findViewById(R.id.name);
//                String text = (String) textView.getText();
//                Article article = (Article) textView.getTag();

//                SharedPreferences settings = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
//
//                SharedPreferences.Editor editor = settings.edit();
//                int keywordCount = settings.getInt("totalKeywords", 0);
//                editor.putString("keyword" + keywordCount, text);
//                editor.commit();
//
//                boolean colourGreen = false;
//                for (int i = 0; i < keywordCount; i++) {
//                    if (settings.getString("keyword" + i, "").equals(text)) {
//                        colourGreen = true;
//                    }
//                    if (colourGreen == true) {
//                        view.setBackgroundColor(Color.parseColor("#00b200"));
//                    } else {
//                        view.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                    }
//                }
            }
        });
        populateList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        populateList();
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

    private void parseNews(String json) {
        JSONArray jsonArray = null;
        String[] news = new String[5];
        try {
            JSONObject jsonObject = new JSONObject(json);
            jsonArray = jsonObject.getJSONArray("hits");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < Math.min(1,jsonArray.length()); i++) {
            try {
                JSONObject jo = jsonArray.getJSONObject(i);
                JSONArray concepts = jo.getJSONArray("concepts");
                for (int j = 0; j < concepts.length(); j++) {
                    Log.v("hojnjfxv",concepts.getJSONObject(j).getString("label"));
                    keywords.add(new Article(concepts.getJSONObject(j).getString("label"), "", concepts.getJSONObject(j).getString("label"), "http://magnacarta800th.com/wp-content/uploads/2015/01/bbc-logo.jpg", concepts.getJSONObject(j).getString("label")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
