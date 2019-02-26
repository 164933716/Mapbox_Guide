package com.mapbox.guide;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ksy.ui.BaseActivity;

import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScrollingActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.chinaMap)
    Button chinaMap;
    @BindView(R.id.globalMap)
    Button globalMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        ButterKnife.bind(this);
        toolbar.setTitle("Guide");
        setSupportActionBar(toolbar);
    }

    @OnClick(R.id.fab)
    public void onViewClicked() {

    }

    @OnClick(R.id.chinaMap)
    public void onChinaMapClicked() {
        Intent intent = new Intent(context, SimpleChinaMapViewActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.globalMap)
    public void onGlobalMapClicked() {
        Intent intent = new Intent(context, SimpleMapViewActivity.class);
        startActivity(intent);
    }
}
