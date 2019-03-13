package com.mapbox.global;

import com.mapbox.guide.App;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import androidx.annotation.NonNull;

public abstract class GlobalBaseActivity extends com.mapbox.guide.MapBaseActivity implements OnMapReadyCallback {
    @Override
    protected void onCreate() {
//        Mapbox.getInstance(context, App.toolToken);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        super.onMapReady(mapboxMap);
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                onMapReady(style);
            }
        });
    }

    @Override
    protected void onMapReady(Style style) {
        super.onMapReady(style);
    }


}
