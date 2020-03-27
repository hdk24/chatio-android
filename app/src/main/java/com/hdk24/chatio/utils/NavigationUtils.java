package com.hdk24.chatio.utils;

import android.app.Activity;
import android.content.Intent;

import com.hdk24.chatio.R;
import com.hdk24.chatio.ui.home.HomeActivity;
import com.hdk24.chatio.ui.login.LoginActivity;

/*
 *  Created by Hanantadk on 27/03/20.
 *  Copyright (c) 2020. All rights reserved.
 *  Last modified 27/03/20.
 */
public final class NavigationUtils {

    private NavigationUtils() {

    }

    public static void goToLoggedIn(Activity activity) {
        activity.startActivity(new Intent(activity, LoginActivity.class));
        activity.overridePendingTransition(R.anim.slide_left, R.anim.stay);
    }

    public static void goToHome(Activity activity) {
        activity.startActivity(new Intent(activity, HomeActivity.class));
    }
}
