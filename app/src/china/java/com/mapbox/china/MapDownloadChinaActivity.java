package com.mapbox.china;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PointF;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.mapbox.guide.App;
import com.mapbox.guide.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;

import org.json.JSONObject;

import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.OnClick;

public class MapDownloadChinaActivity extends ChinaBaseActivity {

    @BindView(R.id.selectionBox)
    FrameLayout selectionBox;
    @BindView(R.id.vEnter)
    Button vEnter;
    @BindView(R.id.vList)
    View vList;
    private OfflineManager offlineManager;
    private OfflineRegion offlineRegion;

    @Override
    protected void onCreate() {
        setContentView(R.layout.activity_map_download);
        vEnter.setEnabled(false);
    }

    @Override
    protected void onMapReady(Style style) {
        super.onMapReady(style);
        offlineManager = OfflineManager.getInstance(context);
        vEnter.setEnabled(true);
    }

    @OnClick(R.id.vEnter)
    public void onViewEnterClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        EditText editText = new EditText(context);
        AlertDialog alertDialog = builder.setMessage("请输入区域名称").setView(editText).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(editText.getText())) {
                    showToast("请输入区域名称");
                    return;
                }
                dialog.dismiss();
                download(editText.getText().toString());
            }
        });
        alertDialog.show();
    }

    private void download(String name) {
        vEnter.setEnabled(false);
        // Set up the OfflineManager
        int top = selectionBox.getTop() - mapView.getTop();
        int left = selectionBox.getLeft() - mapView.getLeft();
        int right = left + selectionBox.getWidth();
        int bottom = top + selectionBox.getHeight();
        LatLng southWest = mapboxMap.getProjection().fromScreenLocation(
                new PointF(left, bottom));
        LatLng northEast = mapboxMap.getProjection().fromScreenLocation(
                new PointF(right, top));
        offlineManager = OfflineManager.getInstance(context);
        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(northEast) // Northeast
                .include(southWest) // Southwest
                .build();
        double minZoom = mapboxMap.getCameraPosition().zoom;
        double maxZoom = mapboxMap.getMaxZoomLevel();
        OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
                mapboxMap.getStyle().getUrl(),
                latLngBounds,
                minZoom,
                maxZoom,
                context.getResources().getDisplayMetrics().density);
// Set the metadata
        byte[] metadata;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("map_name", name);
            jsonObject.put("map_zoom", minZoom);
            String json = jsonObject.toString();
            metadata = json.getBytes("UTF-8");
        } catch (Exception exception) {
            metadata = null;
        }
        offlineManager.createOfflineRegion(definition, metadata,
                new OfflineManager.CreateOfflineRegionCallback() {
                    @Override
                    public void onCreate(OfflineRegion offlineRegion) {
                        showLog("offlineRegion  " + offlineRegion);
                        MapDownloadChinaActivity.this.offlineRegion = offlineRegion;
                        if (offlineRegion != null) {
                            showToast("准备下载");
                            down();
                        } else {
                            onError("获取下载数据失败");
                        }
                    }

                    @Override
                    public void onError(String error) {
                        showLog("Error: " + error);
                        showToast("获取下载数据失败");
                        vEnter.setEnabled(true);
                    }
                });
    }

    private void down() {
        offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
            @Override
            public void onStatusChanged(OfflineRegionStatus status) {
                double percentage = (status.getRequiredResourceCount() >= 0
                        ? (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) :
                        0.0);

                if (status.isComplete()) {
                    // Download complete
                    showLog("Region downloaded successfully.");
                    vEnter.setEnabled(true);
                    vEnter.setText("下载");
                    offlineRegion.setDownloadState(OfflineRegion.STATE_INACTIVE);
                    offlineRegion = null;
                    showToast("下载完成");
                } else if (status.isRequiredResourceCountPrecise()) {
                    showLog("isRequiredResourceCountPrecise " + percentage);
                    vEnter.setText("下载中" + (int) percentage + "%");
                }
            }

            @Override
            public void onError(OfflineRegionError error) {
                // If an error occurs, print to logcat
                showLog("onError reason: " + error.getReason());
                showLog("onError message: " + error.getMessage());
                showToast("下载出错,请稍后重试");
                vEnter.setEnabled(true);
                vEnter.setText("下载");
                offlineRegion.setDownloadState(OfflineRegion.STATE_INACTIVE);
                offlineRegion = null;
            }

            @Override
            public void mapboxTileCountLimitExceeded(long limit) {
                // Notify if offline region exceeds maximum tile count
                showLog("Mapbox tile count limit exceeded: " + limit);
                vEnter.setEnabled(true);
                vEnter.setText("下载");
                showToast("你选择的区域超过了限制,请重新选择");
                offlineRegion.setDownloadState(OfflineRegion.STATE_INACTIVE);
                offlineRegion = null;
            }
        });
        offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);
    }

    @OnClick(R.id.vList)
    public void onViewListClicked() {
        Intent intent = new Intent(context, MapOfflineListActivity.class);
        jump(context, intent);
    }
}
