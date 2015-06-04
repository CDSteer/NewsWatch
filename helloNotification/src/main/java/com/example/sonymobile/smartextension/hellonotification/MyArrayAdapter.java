package com.example.sonymobile.smartextension.hellonotification;

import java.util.ArrayList;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyArrayAdapter extends ArrayAdapter<Article> {

    private final Context context;
    private final ArrayList<Article> itemsArrayList;

    public MyArrayAdapter(Context context, ArrayList<Article> itemsArrayList) {
        super(context, R.layout.news_list_item, itemsArrayList);
        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.news_list_item, parent, false);

        // 3. Get the two text view from the rowView
        TextView labelView = (TextView) rowView.findViewById(R.id.name);
        TextView valueView = (TextView) rowView.findViewById(R.id.details);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageview);

        // 3.5 Adding the whole article as a tag on the title
        labelView.setTag(itemsArrayList.get(position));

        // 4. Set the text for textView
        labelView.setText(itemsArrayList.get(position).getTitle());
        valueView.setText(itemsArrayList.get(position).getDescription());
        imageView.setImageBitmap(itemsArrayList.get(position).getImage());

        if (itemsArrayList.get(position).isInterested() == true) {
            rowView.setBackgroundColor(Color.parseColor("#00b200"));
        } else {
            rowView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        // 5. retrn rowView
        return rowView;
    }
}
