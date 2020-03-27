package com.hdk24.chatio.utils;

/*
 *  Created by Hanantadk on 24/03/20.
 *  Copyright (c) 2020. All rights reserved.
 *  Last modified 24/03/20.
 */
public final class AppConstant {

    private AppConstant() {
        // This utility class is not publicly instantiable
    }

    public static String APP_TAG = "CHAT.IO";

    public static String PREF_FILE_NAME = "com.hdk24.chatio.PREF_NAME_KEY";
    public static final String PREF_SESSION_NAME = "username";
    public static final String PREF_LOGGED_IN = "isLoggedIn";
    public static final String PREF_REMEMBER_ME = "remember_logged_in";

    public static final String CHAT_SERVER_URL = "https://socket-io-chat.now.sh/";
    //public static final String CHAT_SERVER_URL = "http://10.0.2.2:3000/";

    //topic socket
    public static final String TOPIC_JOINED = "user joined";
    public static final String TOPIC_MESSAGE = "new message";
    public static final String TOPIC_LEFT = "user left";
    public static final String TOPIC_TYPING = "typing";
    public static final String TOPIC_STOP_TYPING = "stop typing";

    // type message
    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_LOG = 1;
    public static final int TYPE_ACTION = 2;

    // network status
    public static final int STATUS_DISCONNECTED = 0;
    public static final int STATUS_CONNECTED = 1;
    public static final int STATUS_CONNECTING = 2;
    public static final int STATUS_RECONNECT = 3;
}
