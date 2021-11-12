package com.contact.unmatch;

import android.app.Application;
import android.content.Context;

public class UnmatchApp extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        UnmatchApp.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return UnmatchApp.context;
    }
}
