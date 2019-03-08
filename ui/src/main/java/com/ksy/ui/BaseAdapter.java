/*
 * Copyright 2013 Joan Zapata
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ksy.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseAdapter<T> extends RecyclerView.Adapter {


    private boolean isAlways;
    private int extraSize;
    private ViewInter inter;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = new RecyclerView.ViewHolder(inter.createView(parent, viewType)) {
            @Override
            public String toString() {
                return super.toString();
            }
        };
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLongClickListener != null) {
                    mOnItemLongClickListener.onItemClick(v, position);
                }
                return false;
            }
        });
        convert(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        if (mList == null) {
            return 0;
        }
        if (isAlways) {
            return mList.size() + extraSize;
        } else {
            if (mList.isEmpty()) {
                return 0;
            }
            return mList.size() + extraSize;
        }
    }

    protected OnItemClickListener mOnItemClickListener;
    protected OnItemClickListener mOnItemLongClickListener;
    protected List<T> mList;

    public BaseAdapter(@LayoutRes final int layoutResId, @Nullable List<T> data) {
        ViewInter inter = new ViewInter() {
            @Override
            public View createView(ViewGroup viewGroup, int viewType) {
                return LayoutInflater.from(viewGroup.getContext()).inflate(layoutResId, viewGroup, false);
            }
        };
        this.inter = inter;
        this.mList = data;
    }

    public BaseAdapter(@LayoutRes final int layoutResId, @Nullable List<T> data, int addSize) {
        ViewInter inter = new ViewInter() {
            @Override
            public View createView(ViewGroup viewGroup, int viewType) {
                return LayoutInflater.from(viewGroup.getContext()).inflate(layoutResId, viewGroup, false);
            }
        };
        this.inter = inter;
        this.mList = data;
        this.extraSize = extraSize;
    }


    public BaseAdapter(ViewInter inter, @Nullable List<T> data, int extraSize) {
        this.inter = inter;
        this.mList = data;
        this.extraSize = extraSize;
    }

    public BaseAdapter(ViewInter inter, @Nullable List<T> data, int extraSize, boolean isAlways) {
        this.inter = inter;
        this.mList = data;
        this.extraSize = extraSize;
        this.isAlways = isAlways;
    }

    public BaseAdapter(ViewInter inter, @Nullable List<T> data) {
        this.inter = inter;
        this.mList = data;
    }


    protected abstract void convert(View helper, int position);

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    public interface ViewInter {
        View createView(ViewGroup viewGroup, int viewType);
    }
}
