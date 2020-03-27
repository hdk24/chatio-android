package com.hdk24.chatio.data.preference;

/*
 *  Created by Hanantadk on 24/03/20.
 *  Copyright (c) 2020. All rights reserved.
 *  Last modified 24/03/20.
 */
public interface PrefHelper {

    void saveToString(String prefName, String prefValue);

    void saveToBoolean(String prefName, boolean prefValue);

    void saveToInteger(String prefName, int prefValue);

    String readString(String prefName, String defValue);

    boolean readBoolean(String prefName, boolean defValue);

    int readInteger(String prefName, int defValue);
}
