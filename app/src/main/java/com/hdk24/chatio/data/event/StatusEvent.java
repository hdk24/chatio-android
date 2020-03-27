package com.hdk24.chatio.data.event;

import static com.hdk24.chatio.utils.AppConstant.STATUS_CONNECTED;

/*
 *  Created by Hanantadk on 25/03/20.
 *  Copyright (c) 2020. All rights reserved.
 *  Last modified 25/03/20.
 */
public class StatusEvent {

    private int connectivityStatus = STATUS_CONNECTED;

    public StatusEvent(int connectivityStatus) {
        this.connectivityStatus = connectivityStatus;
    }

    public int getStatus() {
        return connectivityStatus;
    }
}
