package com.hdk24.chatio.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hdk24.chatio.R;
import com.hdk24.chatio.data.model.Message;
import com.hdk24.chatio.ui.base.BaseViewHolder;

import java.util.List;

/*
 *  Created by Hanantadk on 27/03/20.
 *  Copyright (c) 2020. All rights reserved.
 *  Last modified 27/03/20.
 */
public class HomeAdapter extends RecyclerView.Adapter<BaseViewHolder<Message>> {

    private final List<Message> dataSet;
    private final Context context;

    public HomeAdapter(Context context, List<Message> data) {
        this.context = context;
        this.dataSet = data;
    }

    @NonNull
    @Override
    public BaseViewHolder<Message> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_message_item, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<Message> genericHolder, int position) {
        HomeViewHolder holder = (HomeViewHolder) genericHolder;
        holder.bind(dataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void removeMessage(int position) {
        dataSet.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeRemoved(position, dataSet.size());
    }

    public void updateMessage(int from, Message message) {
        dataSet.remove(from);
        dataSet.add(0, message);
        notifyItemMoved(from, 0);
        notifyItemChanged(0);
    }

    public void addNewMessage(Message message) {
        dataSet.add(0, message);
        notifyItemInserted(0);
    }
}
