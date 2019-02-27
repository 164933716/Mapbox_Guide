package com.mapbox.china;

import android.annotation.SuppressLint;

import com.mapbox.guide.App;
import com.mapbox.guide.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;

public class SimpleMapViewActivity extends com.mapbox.china.MapBaseActivity {

    @Override
    protected void onCreate() {
        Mapbox.getInstance(context, App.globalToken);
        setContentView(R.layout.activity_map_global);
    }

    @Override
    protected MapView onMapView() {
        @SuppressLint("NotChinaMapView") MapView mapView = findViewById(R.id.mapView);
        return mapView;
    }
}