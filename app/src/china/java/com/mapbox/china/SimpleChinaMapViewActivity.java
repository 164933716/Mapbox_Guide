package com.mapbox.china;

import com.mapbox.guide.App;
import com.mapbox.guide.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.plugins.china.maps.ChinaMapView;

public class SimpleChinaMapViewActivity extends com.mapbox.china.MapBaseActivity {
    @Override
    protected void onCreate() {
        Mapbox.getInstance(context, App.chinaToken);
        setContentView(R.layout.activity_map_china);
    }
    @Override
    protected MapView onMapView() {
        ChinaMapView mapView = findViewById(R.id.mapView);
        return mapView;
    }
}