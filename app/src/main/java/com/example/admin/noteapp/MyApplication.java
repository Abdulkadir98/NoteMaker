package com.example.admin.noteapp;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by admin on 4/1/2017.
 */

public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
