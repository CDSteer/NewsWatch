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
        startVibrator(0200,0000,1);
    }

    @Override
    protected void setScreenState(final int state) {
        super.setScreenState(state);
    }

    @Override
    protected void sendImage(final int layoutReference, final Bitmap bitmap){
        super.sendImage(layoutReference, bitmap);
    }

    @Override
    protected void showLayout(final int layoutId, final Bundle[] layoutData) {
        super.showLayout(layoutId, layoutData);
    }

    @Override
    protected void startVibrator(int onDuration, int offDuration, int repeats) {
        super.startVibrator(onDuration, offDuration, repeats);
    }
}
