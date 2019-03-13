package com.mapbox.china.demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.guide.App;
import com.mapbox.guide.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.BubbleLayout;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.china.constants.ChinaStyle;
import com.mapbox.mapboxsdk.plugins.china.maps.ChinaMapView;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAnchor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

/**
 * Created by Tianle on 2019-03-11
 */
public class BasicMapActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {


    protected static final int CAMERA_ANIMATION_DURATION = 600;
    protected static final int DEFAULT_CAMERA_ZOOM = 16;
    protected MapboxMap mapboxMap;
    protected ChinaMapView mapView;
    private Context context;
    private List<Feature> allList = new ArrayList<>();
    private GeoJsonSource markersSource;
    private FeatureCollection featureCollection;
    private PermissionsManager permissionsManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, App.chinaToken);
        context = this;
        mapView = new ChinaMapView(context);
        setContentView(mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        Style.Builder builder = new Style.Builder().fromUrl(ChinaStyle.MAPBOX_STREETS_CHINESE);
        mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
            @Override
            public boolean onMapClick(@NonNull LatLng point) {
                Style style = mapboxMap.getStyle();
                if (style != null) {
                    GeoJsonSource source = (GeoJsonSource) style.getSource("custom_markers_source");
                    final PointF pixel = mapboxMap.getProjection().toScreenLocation(point);
                    List<Feature> features = mapboxMap.queryRenderedFeatures(pixel, "custom_marker_layer");
                    if (features != null) {
                        Iterator<Feature> iterator = allList.iterator();
                        while (iterator.hasNext()) {
                            Feature next = iterator.next();
                            for (Feature feature : features) {
                                if (TextUtils.equals(next.getStringProperty("name"), feature.getStringProperty("name"))) {
                                    Boolean selected = feature.getBooleanProperty("selected");
//                                            iterator.remove();
                                    if (selected) {
                                        next.properties().addProperty("selected", false);
                                    } else {
                                        next.properties().addProperty("selected", true);
                                    }
                                }
                            }
                        }
                        source.setGeoJson(FeatureCollection.fromFeatures(allList));
                    }
//                            source.setGeoJson(FeatureCollection.fromFeatures(allList));
                }
                return false;
            }
        });
        this.mapboxMap.setStyle(builder, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent(style);
                mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull LatLng point) {
                        Style style = mapboxMap.getStyle();
                        if (style != null) {
                            GeoJsonSource source = (GeoJsonSource) style.getSource("custom_markers_source");
                            final PointF pixel = mapboxMap.getProjection().toScreenLocation(point);
                            List<Feature> features = mapboxMap.queryRenderedFeatures(pixel, "custom_marker_layer");
                            if (features != null) {
                                Iterator<Feature> iterator = allList.iterator();
                                while (iterator.hasNext()) {
                                    Feature next = iterator.next();
                                    for (Feature feature : features) {
                                        if (TextUtils.equals(next.getStringProperty("name"), feature.getStringProperty("name"))) {
                                            Boolean selected = feature.getBooleanProperty("selected");
//                                            iterator.remove();
                                            if (selected) {
                                                next.properties().addProperty("selected", false);
                                            } else {
                                                next.properties().addProperty("selected", true);
                                            }
                                        }
                                    }
                                }
                                source.setGeoJson(FeatureCollection.fromFeatures(allList));
                            }
//                            source.setGeoJson(FeatureCollection.fromFeatures(allList));
                        }
                        return false;
                    }
                });

                LatLng latLng1 = new LatLng(30.480825277565145d, 114.399720756497d);
                LatLng latLng2 = new LatLng(31.580825277565145d, 111.499720756497d);
                style.addImage("custom_marker", BitmapFactory.decodeResource(context.getResources(), R.drawable.mapbox_marker_icon_default));
                JsonObject jsonObject1 = new JsonObject();
                jsonObject1.addProperty("selected", false);
                jsonObject1.addProperty("name", "jack");

                JsonObject jsonObject2 = new JsonObject();
                jsonObject2.addProperty("selected", false);
                jsonObject2.addProperty("name", "mark");
                allList.add(Feature.fromGeometry(Point.fromLngLat(latLng1.getLongitude(), latLng1.getLatitude()), jsonObject1, "marker1", null));
                allList.add(Feature.fromGeometry(Point.fromLngLat(latLng2.getLongitude(), latLng2.getLatitude()), jsonObject2, "marker2", null));
                featureCollection = FeatureCollection.fromFeatures(allList);
                markersSource = new GeoJsonSource("custom_markers_source", featureCollection);
                style.addSource(markersSource);
                style.addLayer(new SymbolLayer("custom_marker_layer", "custom_markers_source")
                        .withProperties(
                                PropertyFactory.iconAllowOverlap(true),
                                PropertyFactory.iconImage("custom_marker")
                        ));

//                List<Point> points1 = new ArrayList<>();
//                List<Point> points2 = new ArrayList<>();
//                points1.add(Point.fromLngLat(114.399720756497d, 30.480825277565145d));
//                points1.add(Point.fromLngLat(111.499720756497d, 31.580825277565145d));
//
//                points2.add(Point.fromLngLat(100.399720756497d, 36.480825277565145d));
//                points2.add(Point.fromLngLat(122.499720756497d, 33.580825277565145d));
//                List<Feature> features = new ArrayList<>();
//                Feature feature1 = Feature.fromGeometry(LineString.fromLngLats(points1));
//                Feature feature2 = Feature.fromGeometry(LineString.fromLngLats(points2));
//                features.add(feature1);
//                features.add(feature2);
//                style.addSource(new GeoJsonSource("custom_line_source", FeatureCollection.fromFeatures(features)));
//                style.addLayer(new LineLayer("custom_line_layer", "custom_line_source").withProperties(
//                        PropertyFactory.lineWidth(5f),
//                        PropertyFactory.lineColor(Color.parseColor("#e55e5e"))
//                ));
//
//                Layer custom_marker_layer = style.getLayer("custom_line_layer");
//                custom_marker_layer.setProperties(PropertyFactory.visibility(Property.NONE));
//

                style.addLayer(new SymbolLayer("custom_info_layer", "custom_markers_source").withProperties(
                        /* show image with id title based on the value of the name feature property */
                        iconImage("{name}"),
                        /* set anchor of icon to bottom-left */
                        iconAnchor(Property.ICON_ANCHOR_BOTTOM),
                        /* all info window and marker image to appear at the same time*/
                        iconAllowOverlap(true),
                        /* offset the info window to be above the marker */
                        iconOffset(new Float[]{-2f, -20f})

                )
                        /* add a filter to show only when selected feature property is true */
                        .withFilter(eq((get("selected")), literal(true))));
                new GenerateViewIconTask(BasicMapActivity.this).execute(featureCollection);
            }
        });
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // Activate with options
            locationComponent.activateLocationComponent(this, loadedMapStyle);

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(context, "请打开定位相关权限", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(context, "请打开定位相关权限", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void setImageGenResults(HashMap<String, Bitmap> imageMap) {
        if (mapboxMap != null) {
            Style style = mapboxMap.getStyle();
            if (style != null) {
                // calling addImages is faster as separate addImage calls for each bitmap.
                style.addImages(imageMap);
            }
        }
    }

    private void refreshSource() {
        if (markersSource != null && featureCollection != null) {
            markersSource.setGeoJson(featureCollection);
        }
    }

    private static class GenerateViewIconTask extends AsyncTask<FeatureCollection, Void, HashMap<String, Bitmap>> {

        private final HashMap<String, View> viewMap = new HashMap<>();
        private final WeakReference<BasicMapActivity> activityRef;
        private final boolean refreshSource;

        GenerateViewIconTask(BasicMapActivity activity, boolean refreshSource) {
            this.activityRef = new WeakReference<>(activity);
            this.refreshSource = refreshSource;
        }

        GenerateViewIconTask(BasicMapActivity activity) {
            this(activity, false);
        }

        @SuppressWarnings("WrongThread")
        @Override
        protected HashMap<String, Bitmap> doInBackground(FeatureCollection... params) {
            BasicMapActivity activity = activityRef.get();
            if (activity != null) {
                HashMap<String, Bitmap> imagesMap = new HashMap<>();
                LayoutInflater inflater = LayoutInflater.from(activity);

                FeatureCollection featureCollection = params[0];

                for (Feature feature : featureCollection.features()) {

                    BubbleLayout bubbleLayout = (BubbleLayout)
                            inflater.inflate(R.layout.symbol_layer_info_window_layout_callout, null);

                    String name = feature.getStringProperty("name");
                    TextView titleTextView = bubbleLayout.findViewById(R.id.info_window_title);
                    titleTextView.setText(name);

                    int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    bubbleLayout.measure(measureSpec, measureSpec);

                    int measuredWidth = bubbleLayout.getMeasuredWidth();

                    bubbleLayout.setArrowPosition(measuredWidth / 2 - 5);

                    Bitmap bitmap = SymbolGenerator.generate(bubbleLayout);
                    imagesMap.put(name, bitmap);
                    viewMap.put(name, bubbleLayout);
                }

                return imagesMap;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(HashMap<String, Bitmap> bitmapHashMap) {
            super.onPostExecute(bitmapHashMap);
            BasicMapActivity activity = activityRef.get();
            if (activity != null && bitmapHashMap != null) {
                activity.setImageGenResults(bitmapHashMap);
                if (refreshSource) {
                    activity.refreshSource();
                }
            }
        }
    }


    /**
     * Utility class to generate Bitmaps for Symbol.
     */
    private static class SymbolGenerator {

        /**
         * Generate a Bitmap from an Android SDK View.
         *
         * @param view the View to be drawn to a Bitmap
         * @return the generated bitmap
         */
        static Bitmap generate(@NonNull View view) {
            int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            view.measure(measureSpec, measureSpec);

            int measuredWidth = view.getMeasuredWidth();
            int measuredHeight = view.getMeasuredHeight();

            view.layout(0, 0, measuredWidth, measuredHeight);
            Bitmap bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(Color.TRANSPARENT);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            return bitmap;
        }
    }

    protected void animateCamera(LatLng point, boolean zoom) {
        animateCamera(point, zoom, null);
    }

    protected void animateCamera(LatLng point, boolean zoom, MapboxMap.CancelableCallback callback) {
        if (mapboxMap == null) {
            return;
        }
        if (zoom) {
            mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, DEFAULT_CAMERA_ZOOM), CAMERA_ANIMATION_DURATION, callback);
        } else {
            mapboxMap.animateCamera(CameraUpdateFactory.newLatLng(point), CAMERA_ANIMATION_DURATION, callback);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
