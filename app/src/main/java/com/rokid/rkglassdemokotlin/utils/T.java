package com.rokid.rkglassdemokotlin.utils;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rokid.rkglassdemokotlin.R;
import com.rokid.rkglassdemokotlin.app.RKApplication;


public class T {
    private static Toast toast = null;
    //TODO
    public static void show(String text) {
        show(RKApplication.INSTANCE, text);
    }

    public static void show(Context context, String text, int duration) {
        if (null == context)
            return;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            if (null == toast) {
                toast = Toast.makeText(RKApplication.INSTANCE, text, duration);
            } else {
                toast.setText(text);
                toast.setDuration(duration);
            }
            toast.show();
        } else {
            if (null != toast) {
                toast.cancel();
                toast = null;
            }
            toast = Toast.makeText(RKApplication.INSTANCE, text, duration);
            toast.show();
        }
    }

    public static Toast makeToast(Context context, String text, int duration) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_toast_layout, null);
        TextView chapterNameTV = (TextView) view.findViewById(R.id.chapterName);
        chapterNameTV.setText(text);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 200);
        toast.setDuration(duration);
        toast.setView(view);
        return toast;
    }

    //TODO ISSUCCESS
    public static void show(Context context, String text) {
        if (null == context)
            return;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            if (null == toast) {
                toast = makeToast(RKApplication.INSTANCE, text, Toast.LENGTH_LONG);
            } else {
                toast = makeToast(RKApplication.INSTANCE, text, Toast.LENGTH_LONG);
            }
            toast.show();
        } else {
            if (null != toast) {
                toast.cancel();
                toast = null;
            }
            toast = makeToast(RKApplication.INSTANCE, text, Toast.LENGTH_LONG);
            toast.show();
        }

    }
}
