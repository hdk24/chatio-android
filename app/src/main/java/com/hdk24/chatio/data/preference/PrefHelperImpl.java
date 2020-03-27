package com.hdk24.chatio.data.preference;

import android.content.Context;
import android.content.SharedPreferences;

import static com.hdk24.chatio.utils.AppConstant.PREF_FILE_NAME;

/*
 *  Created by Hanantadk on 24/03/20.
 *  Copyright (c) 2020. All rights reserved.
 *  Last modified 24/03/20.
 */
public class PrefHelperImpl implements PrefHelper {

    private final SharedPreferences mPrefs;

    private final SharedPreferences.Editor editor;

    public PrefHelperImpl(Context context) {
        this.mPrefs = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        this.editor = mPrefs.edit();
    }

    @Override
    public void saveToString(String prefName, String prefValue) {
        editor.putString(prefName, prefValue);
        editor.apply();
    }

    @Override
    public void saveToBoolean(String prefName, boolean prefValue) {
        editor.putBoolean(prefName, prefValue);
        editor.apply();
    }

    @Override
    public void saveToInteger(String prefName, int prefValue) {
        editor.putInt(prefName, prefValue);
        editor.apply();
    }

    @Override
    public String readString(String prefName, String defValue) {
        return mPrefs.getString(prefName, defValue);
    }

    @Override
    public boolean readBoolean(String prefName, boolean defValue) {
        return mPrefs.getBoolean(prefName, defValue);
    }

    @Override
    public int readInteger(String prefName, int defValue) {
        return mPrefs.getInt(prefName, defValue);
    }
}
