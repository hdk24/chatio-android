package com.hdk24.chatio.utils;

import com.hdk24.chatio.R;
import com.hdk24.chatio.data.model.Message;

import java.util.ArrayList;
import java.util.List;

import static com.hdk24.chatio.utils.AppConstant.TYPE_MESSAGE;

/*
 *  Created by Hanantadk on 27/03/20.
 *  Copyright (c) 2020. All rights reserved.
 *  Last modified 27/03/20.
 */
public class DataUtils {

    public static List<Message> defaultMessage() {
        List<Message> messageList = new ArrayList<>();
        Message.Builder builder1 = new Message.Builder(TYPE_MESSAGE);
        builder1.time(System.currentTimeMillis());
        builder1.message("let's start create message with socket.io");
        builder1.isPrivate(false);
        builder1.username("Socket.io");

        Message.Builder builder2 = new Message.Builder(TYPE_MESSAGE);
        builder2.time(System.currentTimeMillis());
        builder2.message("chat with your self. Data will save on local");
        builder2.isPrivate(true);
        builder2.username("Saved Message");

        messageList.add(0, builder1.build());
        messageList.add(1, builder2.build());
        return messageList;
    }
}
