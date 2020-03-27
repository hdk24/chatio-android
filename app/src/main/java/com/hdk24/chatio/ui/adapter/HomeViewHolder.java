package com.hdk24.chatio.ui.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.hdk24.chatio.R;
import com.hdk24.chatio.data.model.Message;
import com.hdk24.chatio.ui.base.BaseViewHolder;
import com.hdk24.chatio.utils.ContentUtils;
import com.hdk24.chatio.utils.ImageUtils;

/*
 *  Created by Hanantadk on 27/03/20.
 *  Copyright (c) 2020. All rights reserved.
 *  Last modified 27/03/20.
 */
public class HomeViewHolder extends BaseViewHolder<Message> {

    public HomeViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    protected void bind(Message data) {
        AppCompatImageView imgAvatar = itemView.findViewById(R.id.img_avatar);
        TextView txtUsername = itemView.findViewById(R.id.text_username);
        TextView txtMessage = itemView.findViewById(R.id.text_message);
        TextView txtTime = itemView.findViewById(R.id.text_time);

        imgAvatar.setImageResource(ImageUtils.getDrawableByName(data.getUsername()));
        txtUsername.setText(data.getUsername());
        txtMessage.setText(data.getText());
        txtTime.setText(ContentUtils.timestampToText(context, data.getTime()));
    }
}
