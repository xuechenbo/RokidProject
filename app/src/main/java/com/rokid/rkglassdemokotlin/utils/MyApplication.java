package com.rokid.rkglassdemokotlin.utils;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    private static Context instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = getApplicationContext();

    }

    public static Context getInstance() {
        return instance;
    }


}
