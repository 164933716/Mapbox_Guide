package com.mapbox.china;

import android.graphics.Color;

import com.mapbox.guide.R;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.FillExtrusionLayer;

import butterknife.OnClick;

import static com.mapbox.mapboxsdk.style.expressions.Expression.exponential;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.interpolate;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.expressions.Expression.zoom;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillExtrusionBase;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillExtrusionColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillExtrusionHeight;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillExtrusionOpacity;

public class ChinaBuildingPluginActivity extends ChinaBaseActivity {

    @Override
    protected void onCreate() {
        setContentView(R.layout.activity_map_china_building);
    }

    @Override
    protected void onMapReady(Style style) {
        super.onMapReady(style);
        //此处注释插件代码
//        BuildingPlugin buildingPlugin = new BuildingPlugin(mapView, mapboxMap, style);
//        buildingPlugin.setVisibility(true);
        FillExtrusionLayer fillExtrusionLayer = new FillExtrusionLayer("3d-buildings", "composite");
        fillExtrusionLayer.setSourceLayer("china_building");
//        fillExtrusionLayer.setFilter(eq(get("extrude"), "true"));
        fillExtrusionLayer.setMinZoom(15);
        fillExtrusionLayer.setProperties(
                fillExtrusionColor(Color.LTGRAY),
                fillExtrusionHeight(
                        interpolate(
                                exponential(1f),
                                zoom(),
                                stop(15, literal(0)),
                                stop(16, get("height"))
                        )
                ),
                fillExtrusionBase(get("min_height")),
                fillExtrusionOpacity(0.9f)
        );
        style.addLayer(fillExtrusionLayer);
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
