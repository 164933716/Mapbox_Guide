package com.ksy.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;

/**
 * Created by Ksy.
 */

public class PermissionUtil {
    private static final String TAG = "PermissionUtils";
    //用于在设置界面返回后再次检验
    public static void toAppSetting(Activity activity, int requestCode, String appId) {
        try {
            activity.startActivityForResult(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + appId)),
                    requestCode);
        } catch (Exception ignored) {
        }
    }

    public static void toAppSetting(Fragment fragment, int requestCode, String appId) {
        try {
            fragment.startActivityForResult(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + appId)),
                    requestCode);
        } catch (Exception ignored) {
        }
    }

    public interface PermissionListener {
        void onPermission(boolean isAllPass);
    }


    public static void requestPermission(@NonNull Activity activity, final PermissionListener listener, @NonNull final String... permissions) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        Observable<Permission> observable = rxPermissions.requestEach(permissions);
        observable.observeOn(AndroidSchedulers.mainThread()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Permission>() {
            int index = 0;
            boolean isAllPass = true;

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Permission permission) {
                if (permission.granted) {
                    isAllPass &= true;
                    Log.e(TAG,"用户已经同意该权限    " + permission.name);
                } else if (permission.shouldShowRequestPermissionRationale) {
                    Log.e(TAG,"用户拒绝了该权限  那么下次再次启动时，还会提示请求权限的对话框  " + permission.name);
                    isAllPass &= false;
                } else {
                    Log.e(TAG,"用户拒绝了该权限，并且选中 不再询问 " + permission.name);
                    isAllPass &= false;
                }
                index++;
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                listener.onPermission(isAllPass);
            }
        });

    }

}
