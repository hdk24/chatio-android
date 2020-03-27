package com.hdk24.chatio.utils;

import com.hdk24.chatio.BuildConfig;

import timber.log.Timber;

/*
 *  Created by Hanantadk on 25/03/20.
 *  Copyright (c) 2020. All rights reserved.
 *  Last modified 25/03/20.
 */
public final class AppLogger {

    private AppLogger() {
        // This utility class is not publicly instantiable
    }

    /**
     * show log only on debug mode
     */
    public static void init() {
        if (BuildConfig.DEBUG) Timber.plant(new Timber.DebugTree());
    }

    /**
     * common function to log in debug mode
     *
     * @param message log
     */
    public static void d(String message, Object... args) {
        Timber.d(message, args);
    }
}
