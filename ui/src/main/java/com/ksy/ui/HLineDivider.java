package com.ksy.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.ksy.util.AndroidUtil;

import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by ksy on 2018/3/1.
 */

public class HLineDivider extends RecyclerView.ItemDecoration {

    private int lineLeftMargin;
    private int lineRightMargin;
    private int bottomLineLeftMargin;
    private int bottomLineRightMargin;
    private int lineColor;
    private int lineSize;
    private int bottomLineSize;
    private Paint mPaint;
    private int bgColor;

    public HLineDivider() {
        this(Color.parseColor("#EDEDED"), 0.6f, 0, 0, 0, 0, 0);
    }

    public HLineDivider(int lineLeftMargin) {
        this(Color.parseColor("#EDEDED"), 0.6f, lineLeftMargin, 0, 0f, 0, 0);
    }

    public HLineDivider(float lineSize) {
        this(Color.parseColor("#EDEDED"), lineSize, 0, 0, 0f, 0, 0);
    }

    public HLineDivider(float lineSize, int lineLeftMargin, float bottomLineSize) {
        this(Color.parseColor("#EDEDED"), lineSize, lineLeftMargin, 0, bottomLineSize, 0, 0);
    }

    public HLineDivider(int lineColor, float lineSize) {
        this(lineColor, lineSize, 0, 0, 0.8f, 0, 0);
    }

    public HLineDivider(int lineColor, float lineSize, int lineLeftMargin, int lineRightMargin, float bottomLineSize, int bottomLineLeftMargin, int bottomLineRightMargin) {
        super();
        this.lineSize = AndroidUtil.dip2px(lineSize);
        this.bottomLineSize = AndroidUtil.dip2px(bottomLineSize);
        this.lineLeftMargin = AndroidUtil.dip2px(lineLeftMargin);
        this.lineRightMargin = AndroidUtil.dip2px(lineRightMargin);
        this.bottomLineLeftMargin = AndroidUtil.dip2px(bottomLineLeftMargin);
        this.bottomLineRightMargin = AndroidUtil.dip2px(bottomLineRightMargin);
        this.lineColor = lineColor;
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(this.lineColor);
        //背景颜色
        bgColor = Color.parseColor("#00000000");
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = parent.getChildAt(i);
            int adapterPosition = parent.getChildAdapterPosition(childAt);
            if (adapterPosition != parent.getAdapter().getItemCount() - 1) {
                mPaint.setColor(bgColor);
                c.drawRect(parent.getPaddingLeft(), childAt.getBottom(), parent.getPaddingLeft() + lineLeftMargin, childAt.getBottom() + lineSize, mPaint);
                mPaint.setColor(this.lineColor);
                c.drawRect(parent.getPaddingLeft() + lineLeftMargin, childAt.getBottom(), parent.getWidth() - parent.getPaddingRight() - lineRightMargin, childAt.getBottom() + lineSize, mPaint);
            } else {
                c.drawRect(parent.getPaddingLeft() + bottomLineLeftMargin, childAt.getBottom(), parent.getWidth() - parent.getPaddingRight() - bottomLineRightMargin, childAt.getBottom() + bottomLineSize, mPaint);
            }
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int childAdapterPosition = parent.getChildAdapterPosition(view);
        if (childAdapterPosition != parent.getAdapter().getItemCount() - 1) {
            outRect.bottom = lineSize;
        } else {
            outRect.bottom = bottomLineSize;
        }

    }
}
