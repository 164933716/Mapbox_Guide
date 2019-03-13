package com.ksy.ui;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.collection.SimpleArrayMap;

public class CacheUtil {

    public static final String commonCache = "commonCache";

    private static SimpleArrayMap<String, CacheUtil> cacheUtilMap = new SimpleArrayMap<>();
    private SharedPreferences sp;

    /**
     * 获取APP通用配置
     *
     * @return
     */
    public static CacheUtil getInstance() {
        return getInstance(commonCache);
    }

    /**
     * 获取用户单独配置
     *
     * @return
     */
    public static CacheUtil getInstance(String config4User) {
        CacheUtil spUtils = cacheUtilMap.get(config4User);
        if (spUtils == null) {
            spUtils = new CacheUtil(config4User);
            cacheUtilMap.put(config4User, spUtils);
        }
        return spUtils;
    }

    private CacheUtil(String config4Name) {
        sp = BaseApp.getContext().getSharedPreferences(config4Name, Context.MODE_PRIVATE);
    }

    public void put(@NonNull final String key, @NonNull final String value) {
        sp.edit().putString(key, value).apply();
    }

    public void put(@NonNull final String key, @NonNull final long value) {
        sp.edit().putLong(key, value).apply();
    }

    public void put(@NonNull final String key, @NonNull final boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }

    public void put(@NonNull final String key, @NonNull final Set<String> stringSet) {
        sp.edit().putStringSet(key, stringSet).apply();

    }

    public String getString(@NonNull final String key) {
        return sp.getString(key, "");
    }

    public String getString(@NonNull final String key, String defValue) {
        return sp.getString(key, defValue);
    }

    public long getLong(@NonNull final String key) {
        return sp.getLong(key, -1);
    }

    public boolean getBoolean(@NonNull final String key) {
        return sp.getBoolean(key, false);
    }

    public boolean getBoolean(@NonNull final String key, boolean def) {
        return sp.getBoolean(key, def);
    }


    public Set<String> getStringSet(@NonNull final String key) {
        return getStringSet(key, new HashSet<String>());
    }

    public Set<String> getStringSet(@NonNull final String key, @NonNull final Set<String> defaultValue) {
        return sp.getStringSet(key, defaultValue);
    }


    public void remove(@NonNull final String key) {
        sp.edit().remove(key).apply();
    }

    /**
     * 清除指定sp的集合
     */
    public void clear() {
        sp.edit().clear().apply();
    }
}