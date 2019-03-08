package com.mapbox.china;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ksy.ui.BaseAdapter;
import com.ksy.ui.HLineDivider;
import com.ksy.ui.XRecyclerView;
import com.mapbox.guide.App;
import com.mapbox.guide.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Polygon;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionDefinition;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.plugins.building.BuildingPlugin;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class MapOfflineListActivity extends SimpleChinaMapViewActivity {

    @BindView(R.id.recycler)
    XRecyclerView recyclerView;
    private List<OfflineRegion> allList = new ArrayList<>();
    private BaseAdapter<OfflineRegion> adapter;
    private OfflineRegion selectOfflineRegion;
    private Map<OfflineRegion, Integer> statusMap = new HashMap<>();
    private Polygon polygon;
    private OfflineRegion downloadOfflineRegion;

    @Override
    protected void onCreate() {
        Mapbox.getInstance(context, App.chinaToken);
        setContentView(R.layout.activity_map_offline_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(new HLineDivider());
    }

    @Override
    protected void onMapReady(String style) {
        super.onMapReady(style);
        BuildingPlugin buildingPlugin = new BuildingPlugin(mapView, mapboxMap, style);
        buildingPlugin.setVisibility(true);
        loadData();
    }

    private void loadData() {
        OfflineManager offlineManager = OfflineManager.getInstance(this);
        offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
            @Override
            public void onList(OfflineRegion[] offlineRegions) {
                List<OfflineRegion> offlineRegionsList = null;
                if (offlineRegions != null) {
                    Log.e("TAG", "offlineRegion   " + offlineRegions.length);
                    offlineRegionsList = Arrays.asList(offlineRegions);
                    for (OfflineRegion offlineRegion : offlineRegionsList) {
                        Log.e("TAG", "offlineRegion   " + offlineRegion);
                        Callback callback = new Callback(offlineRegion);
                        offlineRegion.getStatus(callback);
                    }
                }
                initAdapter(offlineRegionsList);
            }

            @Override
            public void onError(String error) {
                showLog(error);
            }
        });
    }

    private void initAdapter(List<OfflineRegion> list) {
        allList.clear();
        if (list != null) {
            allList.addAll(list);
        }
        if (adapter == null) {
            adapter = new BaseAdapter<OfflineRegion>(new BaseAdapter.ViewInter() {
                @Override
                public View createView(ViewGroup viewGroup, int viewType) {
                    View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_map, viewGroup, false);
                    return inflate;
                }
            }, allList) {
                @Override
                protected void convert(View helper, int position) {
                    OfflineRegion offlineRegion = allList.get(position);
                    byte[] metadata = offlineRegion.getMetadata();
                    String regionName = "";
                    double regionZoom = 10;
                    if (metadata != null) {
                        try {
                            String json = new String(metadata, "UTF-8");
                            JSONObject jsonObject = new JSONObject(json);
                            regionName = jsonObject.optString("map_name");
                            regionZoom = jsonObject.optDouble("map_zoom");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    TextView tvName = helper.findViewById(R.id.tvName);
                    View vSet = helper.findViewById(R.id.vSet);

                    if (selectOfflineRegion == offlineRegion) {
                        vSet.setVisibility(View.VISIBLE);
                    } else {
                        vSet.setVisibility(View.GONE);
                    }
                    Integer statusInteger = statusMap.get(offlineRegion);
                    String extra = "";
                    if (statusInteger != null) {
                        int status = statusInteger;
                        if (status >= 100) {
                            extra = ("");
                        } else if (status > 0) {
                            extra = (status + "%");
                        } else {
                            extra = ("无效");
                        }
                    } else {
                        extra = ("");
                    }
                    tvName.setText(regionName + "\u3000" + extra);
                    vSet.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (statusInteger != null) {
                                int status = statusInteger;
                                if (status >= 100) {
                                    delete(offlineRegion);
                                } else if (status > 0) {
                                    downloadOrDelete(offlineRegion);
                                } else {
                                    delete(offlineRegion);
                                }
                            } else {
                                delete(offlineRegion);
                            }
                        }
                    });
                }
            };
            adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    OfflineRegion offlineRegion = allList.get(position);
                    byte[] metadata = offlineRegion.getMetadata();
                    String regionName = "";
                    double regionZoom = 10;
                    if (metadata != null) {
                        try {
                            String json = new String(metadata, "UTF-8");
                            JSONObject jsonObject = new JSONObject(json);
                            regionName = jsonObject.optString("map_name");
                            regionZoom = jsonObject.optDouble("map_zoom");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    OfflineRegionDefinition definition = offlineRegion.getDefinition();
                    LatLngBounds bounds = definition.getBounds();
                    if (selectOfflineRegion == offlineRegion) {
                        mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), regionZoom));
                        return;
                    }
                    if (polygon != null) {
                        mapboxMap.removePolygon(polygon);
                    }
                    selectOfflineRegion = offlineRegion;
                    adapter.notifyDataSetChanged();
                    // Move camera to new position
                    List<LatLng> pointList = new ArrayList<>();
                    pointList.add(bounds.getNorthWest());
                    pointList.add(bounds.getNorthEast());
                    pointList.add(bounds.getSouthEast());
                    pointList.add(bounds.getSouthWest());
                    pointList.add(bounds.getNorthWest());
                    PolygonOptions polygonOptions = new PolygonOptions()
                            .addAll(pointList)
                            .fillColor(Color.parseColor("#1aff0000"));
                    polygon = mapboxMap.addPolygon(polygonOptions);
                    mapboxMap.setLatLngBoundsForCameraTarget(bounds);
                    mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), regionZoom));
                }
            });
            recyclerView.setAdapter(adapter);
        }
        adapter.notifyDataSetChanged();
    }

    private void delete(OfflineRegion offlineRegion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(new String[]{"删除"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (which == 0) {
                    ProgressDialog progressDialog = new ProgressDialog(context);
                    progressDialog.show();
                    offlineRegion.delete(new OfflineRegion.OfflineRegionDeleteCallback() {
                        @Override
                        public void onDelete() {
                            allList.remove(offlineRegion);
                            statusMap.remove(offlineRegion);
                            adapter.notifyDataSetChanged();
                            if (polygon != null) {
                                mapboxMap.removePolygon(polygon);
                            }
                            selectOfflineRegion = null;
                            progressDialog.dismiss();
                            showToast("删除成功");
                        }

                        @Override
                        public void onError(String error) {
                            showLog(error);
                            showToast("删除失败");
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        }).show();
    }

    private void downloadOrDelete(OfflineRegion offlineRegion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(new String[]{"删除", "下载"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (which == 0) {
                    ProgressDialog progressDialog = new ProgressDialog(context);
                    progressDialog.show();
                    offlineRegion.delete(new OfflineRegion.OfflineRegionDeleteCallback() {
                        @Override
                        public void onDelete() {
                            allList.remove(offlineRegion);
                            statusMap.remove(offlineRegion);
                            adapter.notifyDataSetChanged();
                            if (polygon != null) {
                                mapboxMap.removePolygon(polygon);
                            }
                            selectOfflineRegion = null;
                            progressDialog.dismiss();
                            showToast("删除成功");
                        }

                        @Override
                        public void onError(String error) {
                            showLog(error);
                            showToast("删除失败");
                            progressDialog.dismiss();
                        }
                    });
                } else {
                    //暂停上一次的下载
                    if (downloadOfflineRegion != null) {
                        downloadOfflineRegion.setDownloadState(OfflineRegion.STATE_INACTIVE);
                    }
                    offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
                        @Override
                        public void onStatusChanged(OfflineRegionStatus status) {
                            Integer integer = -1;
                            double percentage = (status.getRequiredResourceCount() >= 0
                                    ? (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) :
                                    0.0);
                            if (percentage <= 1) {
                                percentage = 1;
                            }
                            if (status.isComplete()) {
                                showLog("Region downloaded successfully.");
                                integer = 100;
                                offlineRegion.setDownloadState(OfflineRegion.STATE_INACTIVE);
                            } else if (status.isRequiredResourceCountPrecise()) {
                                showLog("isRequiredResourceCountPrecise " + percentage);
                                integer = (int) percentage;
                            }
                            statusMap.put(offlineRegion, integer);
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onError(OfflineRegionError error) {
                            // If an error occurs, print to logcat
                            showLog("onError reason: " + error.getReason());
                            showLog("onError message: " + error.getMessage());
                            showToast("下载出错,请稍后重试");
                            statusMap.put(offlineRegion, -1);
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                            offlineRegion.setDownloadState(OfflineRegion.STATE_INACTIVE);
                        }

                        @Override
                        public void mapboxTileCountLimitExceeded(long limit) {
                            // Notify if offline region exceeds maximum tile count
                            showLog("Mapbox tile count limit exceeded: " + limit);
                            showToast("你选择的区域超过了限制");
                            statusMap.put(offlineRegion, -1);
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                            offlineRegion.setDownloadState(OfflineRegion.STATE_INACTIVE);
                        }
                    });
                    offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);
                    downloadOfflineRegion = offlineRegion;
                }
            }
        }).show();
    }

    private class Callback implements OfflineRegion.OfflineRegionStatusCallback {
        private final OfflineRegion offlineRegion;

        public Callback(OfflineRegion offlineRegion) {
            this.offlineRegion = offlineRegion;
        }

        @Override
        public void onStatus(OfflineRegionStatus status) {
            Integer integer = -1;
            double percentage = (status.getRequiredResourceCount() >= 0
                    ? (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) :
                    0.0);
            if (percentage <= 1) {
                percentage = 1;
            }
            if (status.isComplete()) {
                // Download complete
                showLog("Region downloaded successfully.");
                integer = 100;
            } else if (status.isRequiredResourceCountPrecise()) {
                showLog("isRequiredResourceCountPrecise " + percentage);
                integer = (int) percentage;
            }
            statusMap.put(offlineRegion, integer);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onError(String error) {
            statusMap.put(offlineRegion, -1);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

}
