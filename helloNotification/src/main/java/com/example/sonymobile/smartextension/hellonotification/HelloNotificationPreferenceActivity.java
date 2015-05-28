/*
Copyright (c) 2011, Sony Ericsson Mobile Communications AB
Copyright (c) 2011-2013, Sony Mobile Communications AB

 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 * Neither the name of the Sony Ericsson Mobile Communications AB / Sony Mobile
 Communications AB nor the names of its contributors may be used to endorse or promote
 products derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.example.sonymobile.smartextension.hellonotification;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

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

/**
 * This preference activity lets the user send notifications. It also allows
 * the user to clear all notifications associated with this extension.
 */
public class HelloNotificationPreferenceActivity extends PreferenceActivity {

    private static final int DIALOG_READ_ME = 1;

    private static final int DIALOG_CLEAR = 2;

    /** Notification names. */
    private static final String[] NAMES = new String[] {
            "BBC News"
    };

    /** Notification messages. */
    private static final String[] MESSAGE = new String[] {
            "Have some News"
    };

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource.
        addPreferencesFromResource(R.xml.preferences);
        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Show Readme dialogue.
        Preference preference = findPreference(getText(R.string.preference_key_read_me));
        preference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showDialog(DIALOG_READ_ME);
                return true;
            }
        });

        // Send a notification.
        preference = findPreference(getString(R.string.preference_key_send));
        preference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                addData();
                return true;
            }
        });

        // Show the Clear notifications dialogue.
        preference = findPreference(getString(R.string.preference_key_clear));
        preference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showDialog(DIALOG_CLEAR);
                return true;
            }
        });

        // Remove preferences that are not supported by the accessory.
        if (!ExtensionUtils.supportsHistory(getIntent())) {
            preference = findPreference(getString(R.string.preference_key_clear));
            getPreferenceScreen().removePreference(preference);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;

        // Identify and show the appropriate dialogue on the phone.
        switch (id) {
            case DIALOG_READ_ME:
                dialog = createReadMeDialog();
                break;
            case DIALOG_CLEAR:
                dialog = createClearDialog();
                break;
            default:
                Log.w(HelloNotificationExtensionService.LOG_TAG, "Not a valid dialogue id: " + id);
                break;
        }

        return dialog;
    }

    /**
     * Creates the Readme dialog.
     *
     * @return The dialog.
     */
    private Dialog createReadMeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.preference_option_read_me_txt)
                .setTitle(R.string.preference_option_read_me)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(android.R.string.ok, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }

    /**
     * Creates the Clear all notifications dialog.
     *
     * @return The dialog.
     */
    private Dialog createClearDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.preference_option_clear_txt)
                .setTitle(R.string.preference_option_clear)
                .setIcon(android.R.drawable.ic_input_delete)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        new ClearEventsTask().execute();
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }

    /**
     * Clears all notifications.
     */
    private class ClearEventsTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int nbrDeleted = 0;
            nbrDeleted = NotificationUtil.deleteAllEvents(HelloNotificationPreferenceActivity.this);
            return nbrDeleted;
        }

        @Override
        protected void onPostExecute(Integer id) {
            if (id != NotificationUtil.INVALID_ID) {
                Toast.makeText(HelloNotificationPreferenceActivity.this, R.string.clear_success,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(HelloNotificationPreferenceActivity.this, R.string.clear_failure,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * This method sets randomly generated data that will be connected to a
     * notification.
     */
    private void addData() {
        //Random rand = new Random();
        //int index = rand.nextInt(5);
        // WebServer Request URL
        String serverURL = "http://androidexample.com/media/webservice/JsonReturn.php";
        String theNews = readNews();
        Log.d("JSON:", theNews);
        String[] news = parse(theNews);
        int index =0;
        String name = NAMES[index];
        String message = MESSAGE[index];
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
        String line="";
        String keywords = "London";
        String product = "NewsWeb";
        String content_format = "TextualFormat";
        String recent_first = "yes";
        //String url = "http://data.bbc.co.uk/bbcrd-juicer/articles.json?text=" + keywords + "&product[]=" + product + "&content_format[]=" + content_format + "&recent_first=" + recent_first + "&apikey=3O320TNQSzygKXF8frRiNBQnAANSyUl7";
        String url = "http://data.bbc.co.uk/bbcrd-juicer/articles.json?text=london&product[]=NewsWeb&content_format[]=TextualFormat&recent_first=yes&apikey=3O320TNQSzygKXF8frRiNBQnAANSyUl7";
        HttpGet httpGet = new HttpGet("http://data.bbc.co.uk/bbcrd-juicer/articles.json?text=london&product[]=NewsWeb&content_format[]=TextualFormat&recent_first=yes&apikey=3O320TNQSzygKXF8frRiNBQnAANSyUl7");
        Log.v("Test:", "Hello");
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

    protected String[] parse(String result) {
        String[] news = new String[2];
        try {
            JSONObject jObj = new JSONObject(result);
            JSONArray jsonArray = jObj.getJSONArray("articles");
            //for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject mJsonObject = jsonArray.getJSONObject(0);
                Log.d("OutPut", mJsonObject.getString("title"));
                news[0] = mJsonObject.getString("title");
                news[1] = mJsonObject.getString("description");
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return news;
    }

}
