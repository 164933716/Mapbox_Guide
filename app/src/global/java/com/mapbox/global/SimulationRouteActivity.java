package com.mapbox.global;

import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ksy.util.AndroidUtil;
import com.mapbox.guide.R;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;

import androidx.annotation.NonNull;


public class SimulationRouteActivity extends GlobalBaseActivity {


    private MarkerViewManager markerViewManager;
    private MarkerView srcMarker;
    private MarkerView targetMarker;

    @Override
    protected void onCreate() {
        setContentView(R.layout.activity_simulation_route);
    }

    @Override
    protected void onMapReady(Style style) {
        super.onMapReady(style);
        markerViewManager = new MarkerViewManager(mapView, mapboxMap);
        mapboxMap.addOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
            @Override
            public boolean onMapLongClick(@NonNull LatLng point) {
                if (srcMarker != null && targetMarker != null) {
                    markerViewManager.removeMarker(srcMarker);
                    markerViewManager.removeMarker(targetMarker);
                    srcMarker = null;
                    targetMarker = null;
                }
                if (srcMarker == null) {
                    ImageView imageView = new ImageView(context);
                    imageView.setImageResource(R.drawable.map_marker_dark);
                    imageView.setLayoutParams(new FrameLayout.LayoutParams(AndroidUtil.dip2px(40), AndroidUtil.dip2px(40)));
                    MarkerView markerView = new MarkerView(point, imageView);
                    srcMarker = markerView;
                    markerViewManager.addMarker(srcMarker);
                    return false;
                }
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.map_marker_light);
                imageView.setLayoutParams(new FrameLayout.LayoutParams(AndroidUtil.dip2px(40), AndroidUtil.dip2px(40)));
                MarkerView markerView = new MarkerView(point, imageView);
                targetMarker = markerView;
                markerViewManager.addMarker(targetMarker);
                showToast("找路");
                return false;
            }
        });
    }
//
//    private void fetchRoute() {
//        File offlineRouteFile = Environment.getExternalStoragePublicDirectory("AofflineRoute");
//        if (!offlineRouteFile.exists()) {
//            offlineRouteFile.mkdirs();
//        }
//        MapboxOfflineRouter offlineRouter = new MapboxOfflineRouter(offlineRouteFile.getAbsolutePath());
//        offlineRouter.configure("2018_12_18-20_59_05", new OnOfflineTilesConfiguredCallback() {
//
//            @Override
//            public void onConfigured(int numberOfTiles) {
//// Fetch offline route
//                showLog("numberOfTiles  " + numberOfTiles);
//
//            }
//
//            @Override
//            public void onConfigurationError(@NonNull OfflineError error) {
//// Report error
//                showLog(error);
//            }
//        });
//
//
//        Point srcPoint = Point.fromLngLat(srcMarker.getPosition().getLongitude(), srcMarker.getPosition().getLatitude());
//        Point targetPoint = Point.fromLngLat(targetMarker.getPosition().getLongitude(), targetMarker.getPosition().getLatitude());
//        NavigationRoute.Builder builder = NavigationRoute.builder(this)
//                .accessToken(App.toolToken);
//        builder.origin(srcPoint);
//        if (centerMarker != null) {
//            Point centerPoint = Point.fromLngLat(centerMarker.getPosition().getLongitude(), centerMarker.getPosition().getLatitude());
//            builder.addWaypoint(centerPoint);
//        }
//        builder.destination(targetPoint)
//                .alternatives(true);
//
////        OfflineRoute offlineRoute = OfflineRoute.builder(builder).build();
////        offlineRouter.findRoute(offlineRoute, new OnOfflineRouteFoundCallback() {
////            @Override
////            public void onRouteFound(@NonNull DirectionsRoute route) {
////                showLog(route);
////                showToast("为您找到" + 1 + "条线路");
////                selectRoute = route;
////                mapRoute.addRoute(route);
//////                            startAr.setVisibility(View.VISIBLE);
////                startRoute.setEnabled(true);
////            }
////
////            @Override
////            public void onError(@NonNull OfflineError error) {
////                showLog(error);
////            }
////        });
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
//    }
}

