package com.example.ftpserver;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import java.util.HashMap;

public class MyApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static HashMap<String, String> users;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        users = new HashMap<>();
        users.put("anonymous", "");
        users.put("test", "test");
    }

    public static Context getContext() {
        return context;
    }

    public static HashMap<String, String> getUsers() {
        return users;
    }
}