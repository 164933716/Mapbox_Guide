package com.mapbox.global;

import android.graphics.Color;
import android.view.View;

import com.mapbox.guide.App;
import com.mapbox.guide.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.FillExtrusionLayer;
import com.mapbox.mapboxsdk.style.light.Light;
import com.mapbox.mapboxsdk.style.light.Position;
import com.mapbox.mapboxsdk.utils.ColorUtils;

import androidx.annotation.NonNull;

import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
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

/**
 * Change the location and color of the light that's shined on extrusions
 */
public class AdjustExtrusionLightActivity extends SimpleMapViewActivity {

    private Light light;
    private boolean isRedColor;
    private boolean isInitPosition;

    @Override
    protected void onCreate() {
        Mapbox.getInstance(context, App.globalToken);
        setContentView(R.layout.activity_extrusion_light);
    }

    @Override
    protected void onMapReady(Style style) {
        setupBuildings(style);
        setupLight(style);
    }

    private void setupBuildings(@NonNull Style loadedMapStyle) {
        FillExtrusionLayer fillExtrusionLayer = new FillExtrusionLayer("3d-buildings", "composite");
        fillExtrusionLayer.setSourceLayer("building");
        fillExtrusionLayer.setFilter(eq(get("extrude"), "true"));
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
        loadedMapStyle.addLayer(fillExtrusionLayer);
    }

    private void setupLight(@NonNull Style loadedMapStyle) {
        light = loadedMapStyle.getLight();

        findViewById(R.id.fabLightPosition).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isInitPosition = !isInitPosition;
                if (isInitPosition) {
                    light.setPosition(new Position(1.5f, 90, 80));
                } else {
                    light.setPosition(new Position(1.15f, 210, 30));
                }
            }
        });

        findViewById(R.id.fabLightColor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRedColor = !isRedColor;
                light.setColor(ColorUtils.colorToRgbaString(isRedColor ? Color.RED : Color.BLUE));
            }
        });
    }
}
