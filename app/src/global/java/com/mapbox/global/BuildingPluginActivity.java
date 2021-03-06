package com.mapbox.global;

import com.mapbox.guide.R;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.building.BuildingPlugin;

import butterknife.OnClick;

public final class BuildingPluginActivity extends GlobalBaseActivity {

    @Override
    protected void onCreate() {
        super.onCreate();
        setContentView(R.layout.activity_map_global_building);
    }
    @Override
    protected void onMapReady(Style style) {
        super.onMapReady(style);
        BuildingPlugin buildingPlugin = new BuildingPlugin(mapView, mapboxMap, style);
        buildingPlugin.setVisibility(true);
    }

    @OnClick(R.id.button)
    public void onButtonClicked() {
        animateCamera(new LatLng(30.480825277565145d, 114.399720756497d), false);
    }

    @OnClick(R.id.button1)
    public void onButton1Clicked() {
        animateCamera(new LatLng(41.87827d, -87.62877d), false);
    }
}
