package com.mapbox.guide;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;

public class SimpleMapViewActivity extends MapBaseActivity {

    @Override
    protected MapView onCreateMapView() {
        Mapbox.getInstance(context, App.globalToken);
        return new MapView(context);
    }
}