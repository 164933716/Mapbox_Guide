# Mapbox_Guide
ChinaMap文档：v1.0
# 文档地址 
- [Map介绍地址](https://docs.mapbox.com/android/maps/overview/)
    
- [Demo源码地址](https://github.com/mapbox/mapbox-android-demo)
    
- [plugins源码地址](https://github.com/mapbox/mapbox-plugins-android)

- [plugins介绍地址](https://docs.mapbox.com/android/plugins/overview/china/)



## 前置条件
- Android Studio
- Mapbox Token
- Android设备

## SDK版本
- mapbox-android-sdk:7.2.0　//基础地图ＳＤＫ
- mapbox-android-plugin-china:2.1.0　//中国地图插件
- mapbox-android-plugin-building-v7:0.5.0　//３Ｄ建筑物插件
## Demo源码说明
- 源码截图说明
![](https://i.screenshot.net/jp7yqu2)
- CN与Global区别
![](https://i.screenshot.net/j0ygqsp)
## Android Studio 配置工程
1. 新建一个Android工程　注意：minSdkVersion　最小14
```
    defaultConfig {
        minSdkVersion 19
        targetSdkVersion 19
        versionCode 1
        versionName "1.1"
        vectorDrawables.useSupportLibrary = true
    }
```

2. 在Project的build.gradle文件中配置repositories，添加maven或jcenter仓库地址

```
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven { url "https://mapbox.bintray.com/mapbox" }
    }
```

3. 在主工程的build.gradle文件配置dependencies
``` 
    dependencies {
        implementation fileTree(dir: 'libs', include: ['*.jar'])
        chinaImplementation 'com.mapbox.mapboxsdk:mapbox-android-plugin-china:2.1.0'
        globalImplementation 'com.mapbox.mapboxsdk:mapbox-android-plugin-china:2.1.0'
        chinaImplementation 'com.mapbox.mapboxsdk:mapbox-android-sdk:7.2.0'
        globalImplementation 'com.mapbox.mapboxsdk:mapbox-android-sdk:7.2.0'
        chinaImplementation 'com.mapbox.mapboxsdk:mapbox-android-plugin-building-v7:0.5.0'
        globalImplementation 'com.mapbox.mapboxsdk:mapbox-android-plugin-building-v7:0.5.0'
    }
``` 

## 开发注意事项
- 配置权限
```
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" /> 
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" /> 
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    
```
- Mapbox Token区分,使用CN的token来进行服务请求,CN的token对应ChinaMapView控件，需设置ChinaStyle
- 地图显示需要制定相关的显示风格Style，目前中国地图限定了只有3套官方的style:
  
  |  Style | URI  |
  | ------------ | ------------ |
  |  light-zh-v1 |  mapbox://styles/mapbox/light-zh-v1 |
  |  dark-zh-v1 | mapbox://styles/mapbox/dark-zh-v1  |
  |  streets-zh-v | mapbox://styles/mapbox/dark-zh-v1  |

## 地图相关：
### 显示地图
```
public class BasicMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    protected static final int CAMERA_ANIMATION_DURATION = 600;
    protected static final int DEFAULT_CAMERA_ZOOM = 16;
    protected MapboxMap mapboxMap;
    protected ChinaMapView mapView;
    private Context context;

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
        this.mapboxMap.setStyle(builder, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

            }
        });
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

```

### 显示3D地图
```
public class Plugin3DMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        Style.Builder builder = new Style.Builder().fromUrl(ChinaStyle.MAPBOX_STREETS_CHINESE);
        this.mapboxMap.setStyle(builder, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                       //此处注释插件代码
               //        BuildingPlugin buildingPlugin = new BuildingPlugin(mapView, mapboxMap, style);
               //        buildingPlugin.setVisibility(true);
                       FillExtrusionLayer fillExtrusionLayer = new FillExtrusionLayer("3d-buildings", "composite");
                       fillExtrusionLayer.setSourceLayer("china_building");
                       //此处注释extrude
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
        });
    }
}

```

### 添加Marker
```
        this.mapboxMap.setStyle(builder, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                LatLng latLng1 = new LatLng(30.480825277565145d, 114.399720756497d);
                LatLng latLng2 = new LatLng(31.580825277565145d, 111.499720756497d);
                style.addImage("custom_marker", BitmapFactory.decodeResource(context.getResources(), R.drawable.mapbox_marker_icon_default));
                allList.add(Feature.fromGeometry(Point.fromLngLat(latLng1.getLongitude(), latLng1.getLatitude()), null, "marker1", null));
                allList.add(Feature.fromGeometry(Point.fromLngLat(latLng2.getLongitude(), latLng2.getLatitude()), null, "marker2", null));
                style.addSource(new GeoJsonSource("custom_markers_source", FeatureCollection.fromFeatures(allList)));
                style.addLayer(new SymbolLayer("custom_marker_layer", "custom_markers_source")
                        .withProperties(
                                PropertyFactory.iconAllowOverlap(true),
                                PropertyFactory.iconIgnorePlacement(true),
                                PropertyFactory.iconImage("custom_marker")
                        ));
            }
        });

```
### 添加Line

```
    List<Point> points1 = new ArrayList<>();
    List<Point> points2 = new ArrayList<>();
    points1.add(Point.fromLngLat(114.399720756497d, 30.480825277565145d));
    points1.add(Point.fromLngLat(111.499720756497d, 31.580825277565145d));
    
    points2.add(Point.fromLngLat(100.399720756497d, 36.480825277565145d));
    points2.add(Point.fromLngLat(122.499720756497d, 33.580825277565145d));
    List<Feature> features = new ArrayList<>();
    Feature feature1 = Feature.fromGeometry(LineString.fromLngLats(points1));
    Feature feature2 = Feature.fromGeometry(LineString.fromLngLats(points2));
    features.add(feature1);
    features.add(feature2);
    style.addSource(new GeoJsonSource("custom_line_source", FeatureCollection.fromFeatures(features)));
    style.addLayer(new LineLayer("custom_line_layer", "custom_line_source").withProperties(
              PropertyFactory.lineWidth(5f),
              PropertyFactory.lineColor(Color.parseColor("#e55e5e"))
          ));

```

### 添加图形
```
protected void onMapReady(Style style) {
	super.onMapReady(style);
	startRoute.setEnabled(false);
	String boundingBoxJson = CacheUtil.getInstance().getString("boundingBox");
	if (!TextUtils.isEmpty(boundingBoxJson)) {
		List<List<Point>> POINTS = new ArrayList<>();
		List<Point> points = new ArrayList<>();
		BoundingBox boundingBox = BoundingBox.fromJson(boundingBoxJson);
		Point northeast = boundingBox.northeast();
		Point southwest = boundingBox.southwest();
		Point latLng1 = Point.fromLngLat(northeast.longitude(), northeast.latitude());
		Point latLng2 = Point.fromLngLat(southwest.longitude(), northeast.latitude());
		Point latLng3 = Point.fromLngLat(southwest.longitude(), southwest.latitude());
		Point latLng4 = Point.fromLngLat(northeast.longitude(), southwest.latitude());
		points.add(latLng1);
		points.add(latLng2);
		points.add(latLng3);
		points.add(latLng4);
		points.add(latLng1);
		POINTS.add(points);
		GeoJsonSource offlineSource = new GeoJsonSource("custom_offline_source", Polygon.fromLngLats(POINTS));
		style.addSource(offlineSource);
		FillLayer fillLayer = new FillLayer("custom_offline_layer", "custom_offline_source");
		fillLayer.withProperties(
				fillColor(Color.parseColor("#80A4A4E5")));
		style.addLayer(fillLayer);
	}
}
```
### 添加Map点击事件
```
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
```
### 显示当前位置
```
        this.mapboxMap.setStyle(builder, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent(style);
            }
        });
        
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

```

