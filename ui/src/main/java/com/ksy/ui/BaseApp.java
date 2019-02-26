package com.ksy.ui;

import android.app.Application;

import com.ksy.util.ToastManager;

public class BaseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ToastManager.getInstance().init(this);
    }
}
