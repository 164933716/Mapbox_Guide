package com.mapbox.china;

import com.mapbox.guide.App;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.china.constants.ChinaStyle;

import androidx.annotation.NonNull;

public abstract class ChinaBaseActivity extends com.mapbox.guide.MapBaseActivity implements OnMapReadyCallback {
    @Override
    protected void onCreate() {
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        super.onMapReady(mapboxMap);
        mapboxMap.setStyle(ChinaStyle.MAPBOX_STREETS_CHINESE, new Style.OnStyleLoaded() {
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
