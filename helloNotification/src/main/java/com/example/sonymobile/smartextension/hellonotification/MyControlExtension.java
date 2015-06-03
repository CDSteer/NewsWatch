package com.example.sonymobile.smartextension.hellonotification;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.Dbg;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;

import java.io.ByteArrayOutputStream;

/**
 * Created by cdsteer on 03/06/15.
 */
public class MyControlExtension extends ControlExtension {

    /**
     * Create control extension.
     *
     * @param context            The extension service context.
     * @param hostAppPackageName Package name of host application.
     */
    public MyControlExtension(Context context, String hostAppPackageName) {
        super(context, hostAppPackageName);
    }

    @Override
    public void setScreenState(final int state) {
        if (Dbg.DEBUG) {
            Dbg.d("setScreenState: " + state);
        }
        Intent intent = new Intent(Control.Intents.CONTROL_SET_SCREEN_STATE_INTENT);
        intent.putExtra(Control.Intents.EXTRA_SCREEN_STATE, state);
        sendToHostApp(intent);
    }

    @Override
    public void sendImage(final int layoutReference, final Bitmap bitmap){
        if (Dbg.DEBUG) {
            Dbg.d("sendImage");
        }

        Intent intent = new Intent(Control.Intents.CONTROL_SEND_IMAGE_INTENT);
        intent.putExtra(Control.Intents.EXTRA_LAYOUT_REFERENCE, layoutReference);
        ByteArrayOutputStream os = new ByteArrayOutputStream(256);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        byte[] buffer = os.toByteArray();
        intent.putExtra(Control.Intents.EXTRA_DATA, buffer);
        sendToHostApp(intent);
    }

    @Override
    public void showLayout(final int layoutId, final Bundle[] layoutData) {
        if (Dbg.DEBUG) {
            Dbg.d("showLayout");
        }

        Intent intent = new Intent(Control.Intents.CONTROL_PROCESS_LAYOUT_INTENT);
        intent.putExtra(Control.Intents.EXTRA_DATA_XML_LAYOUT, layoutId);
        if (layoutData != null && layoutData.length > 0) {
            intent.putExtra(Control.Intents.EXTRA_LAYOUT_DATA, layoutData);
        }

        sendToHostApp(intent);
    }
}
