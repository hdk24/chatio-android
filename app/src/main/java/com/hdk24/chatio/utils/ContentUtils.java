package com.hdk24.chatio.utils;

import android.content.Context;

import com.hdk24.chatio.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/*
 *  Created by Hanantadk on 27/03/20.
 *  Copyright (c) 2020. All rights reserved.
 *  Last modified 27/03/20.
 */
public final class ContentUtils {

    /**
     * Get string of time in HH:mm from timestamp
     *
     * @param timestamp timestamp in milliseconds
     * @return string of time
     */
    public static String getStrTimeFromTimestamp(long timestamp, String dateFormat) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();

        /* date formatter in local timezone */
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        sdf.setTimeZone(tz);

        return sdf.format(new Date(timestamp * 1000));
    }

    /**
     * get readable shrinked time for label from timestamp
     *
     * @param timestamp time
     * @return readable time e.g. now, 1m, 1d
     */
    public static String timestampToText(Context context, long timestamp) {
        long currentTime = System.currentTimeMillis();
        long diff = currentTime - timestamp;
        String text;
        if (diff < 60) {
            text = context.getString(R.string.label_now);
        } else if (diff < 60 * 60) {
            text = (int) Math.floor(diff / 60) + context.getString(R.string.label_minutes);
        } else if (diff < 60 * 60 * 24) {
            text = (int) Math.floor(diff / (60 * 60)) + context.getString(R.string.label_hour);
        } else {
            text = getStrTimeFromTimestamp(timestamp, "dd MMM yyyy");
        }
        return text;
    }
}
