package com.mapbox.global;

import android.graphics.PointF;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ksy.ui.BaseAdapter;
import com.ksy.ui.CacheUtil;
import com.ksy.ui.HLineDivider;
import com.ksy.ui.XRecyclerView;
import com.ksy.util.AndroidUtil;
import com.mapbox.geojson.BoundingBox;
import com.mapbox.guide.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.services.android.navigation.v5.navigation.MapboxOfflineRouter;
import com.mapbox.services.android.navigation.v5.navigation.OfflineError;
import com.mapbox.services.android.navigation.v5.navigation.OfflineTiles;
import com.mapbox.services.android.navigation.v5.navigation.OnTileVersionsFoundCallback;
import com.mapbox.services.android.navigation.v5.navigation.RouteTileDownloadListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class RouteDownloadActivity extends GlobalBaseActivity {

    @BindView(R.id.selectionBox)
    FrameLayout selectionBox;
    @BindView(R.id.vEnter)
    Button vEnter;
    @BindView(R.id.recycler)
    XRecyclerView recyclerView;
    private List<String> allList = new ArrayList<>();
    private BaseAdapter<String> adapter;
    private String selectedTile;
    private MapboxOfflineRouter offlineRouter;

    @Override
    protected void onCreate() {
        super.onCreate();
        setContentView(R.layout.activity_map_download);
        vEnter.setEnabled(false);
        recyclerView.setMaxHeight(AndroidUtil.dip2px(220));
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(new HLineDivider());
    }

    @Override
    protected void onMapReady(Style style) {
        super.onMapReady(style);
        File file = new File(Environment.getExternalStorageDirectory(), getString(R.string.appName));
        if (!file.exists()) {
            file.mkdirs();
        }
        offlineRouter = new MapboxOfflineRouter(file.getAbsolutePath());
        offlineRouter.fetchAvailableTileVersions(Mapbox.getAccessToken(), new OnTileVersionsFoundCallback() {
            @Override
            public void onVersionsFound(@NonNull List<String> availableVersions) {
                showLog("onVersionsFound");
                for (String availableVersion : availableVersions) {
                    showLog("availableVersion   " + availableVersion);
                }
                if (availableVersions.size() > 0) {
                    selectedTile = availableVersions.get(0);
                    vEnter.setEnabled(true);
                }
                Collections.reverse(availableVersions);
                initAdapter(availableVersions);
            }

            @Override
            public void onError(@NonNull OfflineError error) {
                showLog("onVersionsFound  onError");
                showToast("Unable to fetch versions");
            }
        });
    }

    private void initAdapter(List<String> list) {
        allList.clear();
        if (list != null) {
            allList.addAll(list);
        }
        if (adapter == null) {
            adapter = new BaseAdapter<String>(new BaseAdapter.ViewInter() {
                @Override
                public View createView(ViewGroup viewGroup, int viewType) {
                    View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_map, viewGroup, false);
                    return inflate;
                }
            }, allList) {
                @Override
                protected void convert(View helper, int position) {
                    String tile = allList.get(position);
                    TextView tvName = helper.findViewById(R.id.tvName);
                    View vSet = helper.findViewById(R.id.vSet);

                    if (TextUtils.equals(tile, selectedTile)) {
                        vSet.setVisibility(View.VISIBLE);
                    } else {
                        vSet.setVisibility(View.GONE);
                    }

                    tvName.setText(tile);
                    vSet.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }
            };
            adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    String tile = allList.get(position);
                    selectedTile = tile;
                    adapter.notifyDataSetChanged();
                }
            });
            recyclerView.setAdapter(adapter);
        }
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.vEnter)
    public void onViewEnterClicked() {
        if (TextUtils.isEmpty(selectedTile)) {
            showToast("请选择tile");
            return;
        }
        if (ContextCompat.checkSelfPermission(
                this, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            downloadSelectedRegion();
        }
    }

    private void downloadSelectedRegion() {
        CacheUtil.getInstance().put("selectTile", selectedTile);
        int top = selectionBox.getTop() - mapView.getTop();
        int left = selectionBox.getLeft() - mapView.getLeft();
        int right = left + selectionBox.getWidth();
        int bottom = top + selectionBox.getHeight();
        LatLng southWest = mapboxMap.getProjection().fromScreenLocation(
                new PointF(left, bottom));
        LatLng northEast = mapboxMap.getProjection().fromScreenLocation(
                new PointF(right, top));
        BoundingBox boundingBox = BoundingBox.fromLngLats(
                southWest.getLongitude(), southWest.getLatitude(),
                northEast.getLongitude(), northEast.getLatitude());
        OfflineTiles.Builder builder = OfflineTiles.builder()
                .accessToken(Mapbox.getAccessToken())
                .version(selectedTile)
                .boundingBox(boundingBox);
        offlineRouter.downloadTiles(builder.build(), new RouteTileDownloadListener() {
            @Override
            public void onError(@NonNull OfflineError error) {
                showToast("There was an error with the download. Please try again.");
                showLog(error.getMessage());
                vEnter.setText("下载");
                vEnter.setEnabled(true);
            }

            @Override
            public void onProgressUpdate(int percent) {
                showLog(percent + "%...");
                vEnter.setEnabled(false);
                vEnter.setText("下载中" + percent + "%");
            }

            @Override
            public void onCompletion() {
                showToast("Download complete");
                vEnter.setText("下载");
                vEnter.setEnabled(true);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if ((grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED)) {
                downloadSelectedRegion();
            } else {
                vEnter.setEnabled(false);
            }
        }
    }
}
