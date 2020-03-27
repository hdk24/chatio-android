package com.hdk24.chatio;

import android.app.Application;
import android.content.Intent;

import com.hdk24.chatio.service.SocketService;
import com.hdk24.chatio.utils.AppLogger;

/*
 *  Created by Hanantadk on 24/03/20.
 *  Copyright (c) 2020. All rights reserved.
 *  Last modified 24/03/20.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // init timber
        AppLogger.init();
    }
}
