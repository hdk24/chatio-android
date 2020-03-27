package com.hdk24.chatio.ui.base;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.hdk24.chatio.data.preference.PrefHelper;
import com.hdk24.chatio.data.preference.PrefHelperImpl;
import com.hdk24.chatio.service.SocketService;

/*
 *  Created by Hanantadk on 25/03/20.
 *  Copyright (c) 2020. All rights reserved.
 *  Last modified 25/03/20.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected abstract ViewBinding bindView();

    protected abstract void onViewReady(Bundle savedInstanceState);

    protected PrefHelper prefHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefHelper = new PrefHelperImpl(this);
        setContentView(bindView().getRoot());
        onViewReady(savedInstanceState);
    }

    /**
     * Start socket service on first time
     */
    public void startSocketService() {
        Intent socketService = new Intent(getApplicationContext(), SocketService.class);
        startService(socketService);
    }

    /**
     * subscribe message event to listen socket
     */
    public void startMessageService() {
        startSocketService();
        SocketService.getInstance().initMessageSubscription();
        bindView().getRoot().post(()->{

        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }
}
