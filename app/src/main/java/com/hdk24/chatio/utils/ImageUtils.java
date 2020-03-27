package com.hdk24.chatio.utils;

import com.hdk24.chatio.R;

/*
 *  Created by Hanantadk on 27/03/20.
 *  Copyright (c) 2020. All rights reserved.
 *  Last modified 27/03/20.
 */
public final class ImageUtils {

    public static int getDrawableByName(String name) {
        if (name.equals("Socket.io")) return R.drawable.ic_socket_holder;
        else if (name.equals("Saved Message")) return R.drawable.ic_bookmark_holder;
        else return R.drawable.bg_avatar;
    }
}
