package com.mapbox.guide;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.ksy.ui.BaseActivity;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.china.constants.ChinaStyle;
import com.mapbox.mapboxsdk.plugins.china.maps.ChinaMapView;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class MapBaseActivity extends BaseActivity implements OnMapReadyCallback {
    protected static final int CAMERA_ANIMATION_DURATION = 800;
    protected static final int DEFAULT_CAMERA_ZOOM = 10;
    protected MapboxMap mapboxMap;
    @SuppressLint("NotChinaMapView")
    protected MapView mapView;
    protected Point monitorGlobalPoint = Point.fromLngLat(-122.396088d, 37.791359);
    protected Point monitorChinaPoint = Point.fromLngLat(114.40998, 30.478783);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_map);
        mapView = onCreateMapView();
        mapView.onCreate(savedInstanceState);
        setContentView(mapView);
        mapView.getMapAsync(this);
    }


    @Override
    public void setContentView(int layoutResID) {
        ViewGroup viewGroup = findViewById(R.id.mapContainer);
        View view = getLayoutInflater().inflate(layoutResID, viewGroup, false);
        setContentView(view);
    }

    @Override
    public void setContentView(View view) {
        ViewGroup viewGroup = findViewById(R.id.mapContainer);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        if (viewGroup != null) {
            viewGroup.removeView(view);
            viewGroup.addView(view, params);
        }
    }

    protected abstract MapView onCreateMapView();


    @SuppressLint("WrongConstant")
    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        if (mapView instanceof ChinaMapView) {
            mapboxMap.setStyle(ChinaStyle.MAPBOX_STREETS_CHINESE, new MapboxMap.OnStyleLoadedListener() {
                @Override
                public void onStyleLoaded(@NonNull String style) {
                    animateCamera(new LatLng(monitorChinaPoint.latitude(), monitorChinaPoint.longitude()), true);
                }
            });
        } else {
            mapboxMap.setStyle(Style.MAPBOX_STREETS, new MapboxMap.OnStyleLoadedListener() {
                @Override
                public void onStyleLoaded(@NonNull String style) {
                    animateCamera(new LatLng(monitorGlobalPoint.latitude(), monitorGlobalPoint.longitude()), true);
                }
            });
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
