package com.mapbox.global;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Button;

import com.google.gson.JsonObject;
import com.ksy.ui.CacheUtil;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.guide.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.ui.v5.route.OnRouteSelectionChangeListener;
import com.mapbox.services.android.navigation.v5.navigation.MapboxOfflineRouter;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.navigation.OfflineError;
import com.mapbox.services.android.navigation.v5.navigation.OfflineRoute;
import com.mapbox.services.android.navigation.v5.navigation.OnOfflineRouteFoundCallback;
import com.mapbox.services.android.navigation.v5.navigation.OnOfflineTilesConfiguredCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.OnClick;

import static com.mapbox.mapboxsdk.style.expressions.Expression.get;


public class SimulationRouteActivityV extends GlobalBaseActivity {


    @BindView(R.id.startRoute)
    Button startRoute;
    private List<LatLng> allList = new ArrayList<>();
    private List<Feature> features = new ArrayList<>();
    private GeoJsonSource markersSource;
    private NavigationMapRoute mapRoute;
    private DirectionsRoute selectRoute;

    @Override
    protected void onCreate() {
        setContentView(R.layout.activity_simulation_route);
    }

    @Override
    protected void onMapReady(Style style) {
        super.onMapReady(style);
        HashMap<String, Bitmap> map = new HashMap<>();
        map.put("marker1", BitmapFactory.decodeResource(context.getResources(), R.drawable.map_marker_light));
        map.put("marker2", BitmapFactory.decodeResource(context.getResources(), R.drawable.map_marker_dark));
        style.addImages(map);
        FeatureCollection featureCollection = FeatureCollection.fromFeatures(new ArrayList<>());
        markersSource = new GeoJsonSource("custom_markers_source", featureCollection);
        style.addSource(markersSource);
        SymbolLayer symbolLayer = new SymbolLayer("custom_marker_layer", "custom_markers_source");
        symbolLayer.withProperties(
                PropertyFactory.iconAllowOverlap(true),
                PropertyFactory.iconImage(get("name"))
        );
        style.addLayer(symbolLayer);
        mapRoute = new NavigationMapRoute(mapView, mapboxMap);
        mapRoute.setOnRouteSelectionChangeListener(new OnRouteSelectionChangeListener() {
            @Override
            public void onNewPrimaryRouteSelected(DirectionsRoute directionsRoute) {
                selectRoute = directionsRoute;
            }
        });
        mapboxMap.addOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
            @Override
            public boolean onMapLongClick(@NonNull LatLng point) {
                startRoute.setEnabled(false);
                mapRoute.updateRouteVisibilityTo(false);
                mapRoute.updateRouteArrowVisibilityTo(false);
                if (allList.size() >= 2) {
                    allList.clear();
                    features.clear();
                }
                if (allList.size() == 0) {
                    //画点
                    JsonObject jsonObject1 = new JsonObject();
                    jsonObject1.addProperty("name", "marker1");
                    features.add(Feature.fromGeometry(Point.fromLngLat(point.getLongitude(), point.getLatitude()), jsonObject1, "marker1", null));
                    allList.add(point);
                } else if (allList.size() == 1) {
                    //画点
                    JsonObject jsonObject2 = new JsonObject();
                    jsonObject2.addProperty("name", "marker2");
                    features.add(Feature.fromGeometry(Point.fromLngLat(point.getLongitude(), point.getLatitude()), jsonObject2, "marker2", null));
                    allList.add(point);
                    showToast("找路");
                    fetchRoute();
                }
                markersSource.setGeoJson(FeatureCollection.fromFeatures(features));
                return false;
            }
        });
    }

    private void fetchRoute() {
        String selectTile = CacheUtil.getInstance().getString("selectTile");
        if (TextUtils.isEmpty(selectTile)) {
            showToast("tile 匹配不成功");
            return;
        }
        File file = new File(Environment.getExternalStorageDirectory(), getString(R.string.appName));
        if (!file.exists()) {
            file.mkdirs();
        }

        LatLng srcMarker = allList.get(0);
        LatLng targetMarker = allList.get(1);
        Point srcPoint = Point.fromLngLat(srcMarker.getLongitude(), srcMarker.getLatitude());
        Point targetPoint = Point.fromLngLat(targetMarker.getLongitude(), targetMarker.getLatitude());
        NavigationRoute.Builder builder = NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken());
        builder.origin(srcPoint);
        builder.destination(targetPoint)
                .alternatives(true);

        //离线导航
        OfflineRoute offlineRoute = OfflineRoute.builder(builder).build();
        MapboxOfflineRouter offlineRouter = new MapboxOfflineRouter(file.getAbsolutePath());
        offlineRouter.configure(selectTile, new OnOfflineTilesConfiguredCallback() {

            @Override
            public void onConfigured(int numberOfTiles) {
                // Fetch offline route
                showLog("onConfigured  " + numberOfTiles);
                offlineRouter.findRoute(offlineRoute, new OnOfflineRouteFoundCallback() {
                    @Override
                    public void onRouteFound(@NonNull DirectionsRoute route) {
                        showLog(route);
                        showToast("为您找到" + 1 + "条线路");
                        selectRoute = route;
                        mapRoute.addRoute(route);
                        startRoute.setEnabled(true);
                    }

                    @Override
                    public void onError(@NonNull OfflineError error) {
                        showLog(error.getMessage());
                        startRoute.setEnabled(false);
                    }
                });
            }

            @Override
            public void onConfigurationError(@NonNull OfflineError error) {
                // Report error
                showLog("onConfigurationError   " + error);
            }
        });

//        builder.build()
//                .getRoute(new Callback<DirectionsResponse>() {
//                    @Override
//                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
//                        if (response != null && response.body() != null && !response.body().routes().isEmpty()) {
//                            List<DirectionsRoute> routes = response.body().routes();
//                            showLog("routes.size()  " + routes.size());
//                            showToast("为您找到" + routes.size() + "条线路");
//                            selectRoute = routes.get(0);
//                            mapRoute.addRoutes(routes);
////                            startAr.setVisibility(View.VISIBLE);
//                            startRoute.setEnabled(true);
//                        } else {
//                            onFailure(null, null);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
//                        showToast("线路数据未找到");
////                        startAr.setVisibility(View.GONE);
//                        selectRoute = null;
//                        startRoute.setEnabled(false);
//                    }
//                });
    }

    @OnClick(R.id.startRoute)
    public void onViewClicked() {

    }
}

