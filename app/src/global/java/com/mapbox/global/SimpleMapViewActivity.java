package com.mapbox.global;

import com.mapbox.guide.App;
import com.mapbox.guide.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.Style;

public class SimpleMapViewActivity extends MapBaseActivity {

    @Override
    protected void onCreate() {
        Mapbox.getInstance(context, App.globalToken);
        setContentView(R.layout.activity_map_global);
    }

    @Override
    protected MapView onMapView() {
        MapView mapView = findViewById(R.id.mapView);
        return mapView;
    }

    @Override
    protected void onMapReady(Style style) {

    }
}