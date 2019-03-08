package com.ksy.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class XRecyclerView extends RecyclerView {

    public void setMaxHeight(int mMaxHeight) {
        this.mMaxHeight = mMaxHeight;
    }

    private int mMaxHeight;

    public XRecyclerView(Context context) {
        super(context);
    }

    public XRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public XRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMaxHeight > 0) {
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(mMaxHeight, View.MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
