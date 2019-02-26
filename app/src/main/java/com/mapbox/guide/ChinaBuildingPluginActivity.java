package com.mapbox.guide;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.plugins.building.BuildingPlugin;
import com.mapbox.mapboxsdk.plugins.china.maps.ChinaMapView;

import butterknife.OnClick;

public class ChinaBuildingPluginActivity extends SimpleChinaMapViewActivity {

    @Override
    protected void onCreate() {
        Mapbox.getInstance(context, App.chinaToken);
        setContentView(R.layout.activity_map_china_building);
    }

    @Override
    protected void onMapReady(String style) {
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
