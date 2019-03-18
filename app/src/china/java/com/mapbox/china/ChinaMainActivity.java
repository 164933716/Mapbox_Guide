package com.mapbox.china;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ksy.ui.BaseActivity;
import com.ksy.util.AndroidUtil;
import com.mapbox.china.demo.BasicMapActivity;
import com.mapbox.guide.R;

import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;

public class ChinaMainActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.buttonContainer)
    LinearLayout buttonContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar.setTitle("Guide");
        setSupportActionBar(toolbar);
        createButton("地图展示", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jump(context, BasicMapActivity.class);
            }
        });
        createButton("3D地图展示", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jump(context, ChinaBuildingPluginActivity.class);
            }
        });
        createButton("地图下载", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jump(context, MapDownloadChinaActivity.class);
            }
        });
    }

    private Button createButton(String text, View.OnClickListener onClickListener) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = AndroidUtil.dip2px(8);
        Button button = new Button(context);
        button.setLayoutParams(params);
        button.setText(text);
        button.setOnClickListener(onClickListener);
        buttonContainer.addView(button);
        return button;
    }


}
