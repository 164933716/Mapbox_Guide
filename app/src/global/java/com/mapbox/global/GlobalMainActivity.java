package com.mapbox.global;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ksy.ui.BaseActivity;
import com.ksy.util.AndroidUtil;
import com.mapbox.guide.R;

import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;

public class GlobalMainActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.container)
    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar.setTitle("Guide");
        setSupportActionBar(toolbar);
        createButton("地图", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jump(context, SimpleMapViewActivity.class);
            }
        });
        createButton("3D地图", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jump(context, BuildingPluginActivity.class);
            }
        });
        createButton("导航下载", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jump(context, RouteDownloadActivity.class);
            }
        });
        createButton("模拟导航", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jump(context, SimulationRouteActivityV.class);
            }
        });

    }

    private Button createButton(String text, View.OnClickListener onClickListener) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = AndroidUtil.dip2px(8);
        Button button = new Button(context);
        button.setText(text);
        button.setOnClickListener(onClickListener);
        container.addView(button, params);
        return button;
    }

}
