package com.mapbox.global;

import com.mapbox.guide.R;
import com.mapbox.mapboxsdk.maps.Style;

public final class SimpleMapViewActivity extends GlobalBaseActivity {
    @Override
    protected void onCreate() {
        super.onCreate();
        setContentView(R.layout.activity_map_global);
    }

    @Override
    protected void onMapReady(Style style) {
        super.onMapReady(style);
    }
}