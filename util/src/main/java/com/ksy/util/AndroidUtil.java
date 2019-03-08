package com.ksy.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import androidx.core.content.FileProvider;

public class AndroidUtil {

    public static int dip2px(float dp) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * ((float) displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) px;
    }


    public static Uri getFileUri(Context context, File file) {
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileurl", file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }

    public static void takeImage(Activity activity, File tempImageFile, int requestCode) {
        Uri uriImage = AndroidUtil.getFileUri(activity, tempImageFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriImage);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void chooseImage(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activity.startActivityForResult(intent, requestCode);//打开相册
    }

    public static void chooseVideo(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        activity.startActivityForResult(intent, requestCode);//打开相册
    }

    public static void takeVideo(Activity activity, File tempVideoFile, int requestCode) {
        Uri uriImage = AndroidUtil.getFileUri(activity, tempVideoFile);
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriImage);
        activity.startActivityForResult(intent, requestCode);
    }

    private static File getStorageDir(Context context) {
        File var1 = Environment.getExternalStorageDirectory();
        if (var1.exists()) {
            return var1;
        }
        return context.getFilesDir();
    }

    public static File getTempImageFile(Context context) {
        String tempImageFolder = "Ksy/tempImage";
        File dir = new File(getStorageDir(context), tempImageFolder);
        File file = new File(dir, getTempImageName());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (file.exists()) {
            file.delete();
        }
        return file;
    }

    public static File getTempVideoFile(Context context) {
        String tempImageFolder = "Ksy/tempVideo";
        File dir = new File(getStorageDir(context), tempImageFolder);
        File file = new File(dir, getTempVideoName());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (file.exists()) {
            file.delete();
        }
        return file;
    }

    public static String getTempImageName() {
        String imageExt = ".jpeg";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String formatDate = dateFormat.format(new Date());
        return formatDate + "_" + getRandom() + imageExt;
    }

    public static String getTempVideoName() {
        String imageExt = ".mp4";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String formatDate = dateFormat.format(new Date());
        return formatDate + "_" + getRandom() + imageExt;
    }

    public static int getRandom() {
        Random random = new Random();
        int s = random.nextInt(9999) % (9999 - 1000 + 1) + 1000;
        return s;
    }

    public static String getRealPathFromUri_AboveApi19(Context context, Uri uri) {
        String filePath = null;
        String wholeID = DocumentsContract.getDocumentId(uri);

        // 使用':'分割
        String id = wholeID.split(":")[1];

        String[] projection = {MediaStore.Images.Media.DATA};
        String selection = MediaStore.Images.Media._ID + "=?";
        String[] selectionArgs = {id};

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,//
                projection, selection, selectionArgs, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndex(projection[0]);
            if (cursor.moveToFirst()) filePath = cursor.getString(columnIndex);
            cursor.close();
        }
        return filePath;
    }

    public static String getRealVideoPathFromUri_AboveApi19(Context context, Uri uri) {
        String filePath = null;
        String wholeID = DocumentsContract.getDocumentId(uri);

        // 使用':'分割
        String id = wholeID.split(":")[1];

        String[] projection = {MediaStore.Video.Media.DATA};
        String selection = MediaStore.Video.Media._ID + "=?";
        String[] selectionArgs = {id};

        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,//
                projection, selection, selectionArgs, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndex(projection[0]);
            if (cursor.moveToFirst()) filePath = cursor.getString(columnIndex);
            cursor.close();
        }
        return filePath;
    }

    public static boolean fileIsError(File file) {
        if (file == null || !file.exists() || !file.isFile() || !file.canRead() || file.length() <= 0) {
            return true;
        }
        return false;
    }
}
