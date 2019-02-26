package com.mapbox.guide;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.plugins.china.constants.ChinaStyle;
import com.mapbox.mapboxsdk.plugins.china.maps.ChinaMapView;

import androidx.annotation.NonNull;

public class SimpleChinaMapViewActivity extends MapBaseActivity {
    @Override
    protected MapView onCreateMapView() {
        Mapbox.getInstance(context,App.chinaToken);
        return new ChinaMapView(context);
    }

}