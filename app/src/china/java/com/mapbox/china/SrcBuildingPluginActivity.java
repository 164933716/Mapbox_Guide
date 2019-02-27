package com.mapbox.china;

import android.os.Bundle;

import com.mapbox.guide.App;
import com.mapbox.guide.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.building.BuildingPlugin;
import com.mapbox.mapboxsdk.plugins.china.constants.ChinaStyle;
import com.mapbox.mapboxsdk.plugins.china.maps.ChinaMapView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 *  Use the buildings plugin to display buildings' heights (extrusions) in 3D.
 */
public class SrcBuildingPluginActivity extends AppCompatActivity {

  private ChinaMapView mapView;
  private BuildingPlugin buildingPlugin;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Mapbox access token is configured here. This needs to be called either in your application
    // object or in the same activity which contains the mapview.
    Mapbox.getInstance(this, App.chinaToken);

    setContentView(R.layout.activity_building_plugin);

    mapView = findViewById(R.id.mapView);
    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync(new OnMapReadyCallback() {
      @Override
      public void onMapReady(@NonNull final MapboxMap map) {
        map.setStyle(ChinaStyle.MAPBOX_STREETS_CHINESE, new MapboxMap.OnStyleLoadedListener() {
          @Override
          public void onStyleLoaded(@NonNull String style) {
            buildingPlugin = new BuildingPlugin(mapView, map, style);
            buildingPlugin.setMinZoomLevel(15f);
            buildingPlugin.setVisibility(true);
          }
        });
      }
    });
  }

  @Override
  protected void onStart() {
    super.onStart();
    mapView.onStart();
  }

  @Override
  protected void onResume() {
    super.onResume();
    mapView.onResume();
  }

  @Override
  protected void onPause() {
    super.onPause();
    mapView.onPause();
  }

  @Override
  protected void onStop() {
    super.onStop();
    mapView.onStop();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mapView.onSaveInstanceState(outState);
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mapView.onLowMemory();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mapView.onDestroy();
  }
}
