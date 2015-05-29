package com.example.sonymobile.smartextension.hellonotification;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by cdsteer on 03/04/15.
 */
public class MyList extends SimpleCursorAdapter {

    public MyList(Context context, int layout, MatrixCursor c, String[] from, int[] to) {
        super(context, layout, c, from, to);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor){
        MatrixCursor mc = (MatrixCursor)cursor;
        TextView bodyText = (TextView) view.findViewById(R.id.name);
        bodyText.setText(mc.getString(mc.getColumnIndex("name")));
        TextView typeText = (TextView) view.findViewById(R.id.details);
        typeText.setText(mc.getString(mc.getColumnIndex("description")));
    }

}
