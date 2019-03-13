package com.ksy.ui;

import android.app.Application;
import android.content.Context;

import com.ksy.util.ToastManager;

public class BaseApp extends Application {
   static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
        ToastManager.getInstance().init(this);
    }

    public static Context getContext() {
        return context;
    }
}
