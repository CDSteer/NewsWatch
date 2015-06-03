package com.example.sonymobile.smartextension.hellonotification;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by cdsteer on 31/05/15.
 */
public class ViewSavedNews extends Activity{

    private ListAdapter adapter;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_list);
        listView = (ListView) findViewById (R.id.news_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                TextView textView = (TextView) view.findViewById(R.id.name);
//                String text = (String) textView.getText();
//                savedNews.add(text);
            }
        });
        populateList();
    }

    private void populateList(){
        ArrayList<Article> userNews = new ArrayList();
        ArrayList<Article> articles = NewsReadService.getArticles();
        for (int i = 0; i < articles.size(); i++) {
            if (articles.get(i).isInterested() == true) {
                userNews.add(articles.get(i));
            }
        }
        adapter = new MyArrayAdapter(this, userNews);
        listView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        populateList();
    }
}
