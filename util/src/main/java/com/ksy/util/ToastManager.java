package com.ksy.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;


public class ToastManager {
    private static ToastManager instance;
    private Context context;
    private Toast toast;
    private Handler handler;

    private ToastManager() {
        handler = new Handler(Looper.getMainLooper());
    }

    public static ToastManager getInstance() {
        if (instance == null) {
            synchronized (ToastManager.class) {
                if (instance == null) {
                    instance = new ToastManager();
                    return instance;
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        if (context != null) {
            this.context = context.getApplicationContext();
        }
    }

    public void show(final CharSequence msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (context == null) {
                    return;
                }
                if (toast == null) {
                    toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
                }
                toast.setText(msg);
                toast.show();
            }
        });

    }

}
