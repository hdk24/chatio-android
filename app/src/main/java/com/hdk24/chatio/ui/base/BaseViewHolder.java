package com.hdk24.chatio.ui.base;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/*
 *  Created by Hanantadk on 27/03/20.
 *  Copyright (c) 2020. All rights reserved.
 *  Last modified 27/03/20.
 */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    protected final Context context;

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
        this.context = itemView.getContext();
    }

    protected abstract void bind(T data);
}
