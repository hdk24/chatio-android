package com.hdk24.chatio.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.hdk24.chatio.data.event.MessageEvent;
import com.hdk24.chatio.data.event.StatusEvent;
import com.hdk24.chatio.data.preference.PrefHelper;
import com.hdk24.chatio.data.preference.PrefHelperImpl;
import com.hdk24.chatio.utils.AppConstant;
import com.hdk24.chatio.utils.AppLogger;
import com.hdk24.chatio.utils.SocketUtils;

import org.greenrobot.eventbus.EventBus;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.transports.WebSocket;

import static com.hdk24.chatio.utils.AppConstant.PREF_LOGGED_IN;
import static com.hdk24.chatio.utils.AppConstant.PREF_SESSION_NAME;
import static com.hdk24.chatio.utils.AppConstant.STATUS_CONNECTED;
import static com.hdk24.chatio.utils.AppConstant.STATUS_CONNECTING;
import static com.hdk24.chatio.utils.AppConstant.STATUS_DISCONNECTED;
import static com.hdk24.chatio.utils.AppConstant.TOPIC_ADD_USER;
import static com.hdk24.chatio.utils.AppConstant.TOPIC_JOINED;
import static com.hdk24.chatio.utils.AppConstant.TOPIC_LEFT;
import static com.hdk24.chatio.utils.AppConstant.TOPIC_MESSAGE;
import static com.hdk24.chatio.utils.AppConstant.TOPIC_STOP_TYPING;
import static com.hdk24.chatio.utils.AppConstant.TOPIC_TYPING;

/*
 *  Created by Hanantadk on 24/03/20.
 *  Copyright (c) 2020. All rights reserved.
 *  Last modified 24/03/20.
 */
public class SocketService extends Service {

    private static SocketService mInstance;
    private final String[] transport = {WebSocket.NAME};
    private final String[] messageEvent = {
            TOPIC_MESSAGE, TOPIC_JOINED,
            TOPIC_LEFT, TOPIC_TYPING, TOPIC_STOP_TYPING};
    public List<String> listSubscription = new ArrayList<>();
    private Disposable connectivitySubscription;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private EventBus bus = EventBus.getDefault();
    private Socket mSocket;
    private PrefHelper prefHelper;
    private boolean connected;

    /**
     * make it synchronized so there is only one access at a time
     */
    public static synchronized SocketService getInstance() {
        return mInstance;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mInstance = this;
        prefHelper = new PrefHelperImpl(this);
        initialiseConnectionDetector();
        connect();
        return START_NOT_STICKY;
    }

    /**
     * check internet connection status
     */
    private void initialiseConnectionDetector() {
        connectivitySubscription = ReactiveNetwork.observeInternetConnectivity()
                .flatMapSingle(connectivity -> ReactiveNetwork.checkInternetConnectivity())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(aBoolean -> {
                    connectivitySubscription.dispose();
                    if (aBoolean) {
                        AppLogger.d("SOCKET:: INTERNET RECONNECT");
                        if (mSocket != null) mSocket.connect();
                        else disconnect();
                    }
                }, throwable -> disconnect());
        compositeDisposable.add(connectivitySubscription);
    }

    /**
     * disconnect socket when no internet or error reach connection
     */
    private void disconnect() {
        connected = false;
        if (mSocket != null) mSocket.disconnect();
        AppLogger.d("SOCKET:: Connection - FAILED");
    }

    /**
     * initialize and make connection to socket
     */
    public void connect() {
        // init socket configuration
        try {
            AppLogger.d("SOCKET:: Connection - INITIALIZE...");
            IO.Options options = new IO.Options();
            options.forceNew = true;
            options.transports = transport;
            options.secure = true;
            options.reconnection = true;
            options.reconnectionDelay = 5000;
            mSocket = IO.socket(AppConstant.CHAT_SERVER_URL, options);

        } catch (URISyntaxException e) {
            AppLogger.d(e.getMessage());
            connected = false;
            bus.post(new StatusEvent(STATUS_DISCONNECTED));
        }

        // init event subscription
        onSubscribeConnectionEvent();

        // init subscribe event
        if (!connected) mSocket.connect();
    }

    /**
     * get socket and use to other class
     *
     * @return socket
     */
    public Socket getSocket() {
        return mSocket;
    }

    /**
     * handle socket subscription and broadcast status
     */
    private void onSubscribeConnectionEvent() {
        // subscribe when socket connected
        mSocket.on(Socket.EVENT_CONNECT, args -> {
            AppLogger.d("SOCKET:: Connection - CONNECTED ");
            connected = true;
            EventBus.getDefault().post(new StatusEvent(STATUS_CONNECTED));
            registerUsername();
        });

        // subscribe when socket on connecting
        mSocket.on(Socket.EVENT_CONNECTING, args -> {
            AppLogger.d("SOCKET:: Connection - CONNECTING ");
            connected = false;
            EventBus.getDefault().post(new StatusEvent(STATUS_CONNECTING));
        });

        // subscribe when socket disconnect
        mSocket.on(Socket.EVENT_DISCONNECT, args -> {
            String msg = args != null && args.length > 0 ? args[0].toString() : "";
            AppLogger.d("SOCKET:: Connection - DISCONNECT - " + msg);
            connected = false;
            EventBus.getDefault().post(new StatusEvent(STATUS_DISCONNECTED));
        });

        // subscribe when socket connection error
        mSocket.on(Socket.EVENT_ERROR, args -> {
            AppLogger.d("SOCKET:: Connection - ERROR - " + args[0].toString());
            connected = false;
            EventBus.getDefault().post(new StatusEvent(STATUS_DISCONNECTED));
        });

        // subscribe when socket reconnect
        mSocket.on(Socket.EVENT_RECONNECT, args -> {
            AppLogger.d("SOCKET:: Connection - RECONNECT - " + args[0].toString());
            connected = false;
            EventBus.getDefault().post(new StatusEvent(STATUS_CONNECTING));
        });

        // check count reconnect with delay
        mSocket.on(Socket.EVENT_RECONNECT_ATTEMPT, args -> {
            AppLogger.d("SOCKET:: Connection - RECONNECT ATTEMPT - " + args[0].toString());
            connected = false;
        });
    }

    /**
     * subscribe message event when user is loggedIn
     */
    public void initMessageSubscription() {
        boolean isLoggedIn = prefHelper.readBoolean(PREF_LOGGED_IN, false);
        if (!isLoggedIn) return;
        for (String event : messageEvent) {
            if (!listSubscription.contains(event)) {
                AppLogger.d("SOCKET:: event - " + event);
                listSubscription.add(event);
                subscribeEvent(event);
            }
        }
    }

    /**
     * subscribe event
     * when success broadcast event by topic,
     * when error subscribe, do resubscribe eent only when connected
     *
     * @param event type
     */
    private void subscribeEvent(String event) {
        Disposable subscription = SocketUtils.makeSubscription(mSocket, event)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    AppLogger.d("SOCKET:: subscribe - " + event + " data: " + s);
                    MessageEvent msg = new MessageEvent();
                    msg.topic = event;
                    msg.message = s;
                    msg.time = System.currentTimeMillis() / 1000;
                    bus.post(msg);

                }, throwable -> {
                    AppLogger.d("SOCKET:: subscribe - ERROR ");
                    if (connected) subscribeEvent(event);
                });
        compositeDisposable.add(subscription);
    }

    /**
     * add user to socket
     * disconnect mean left group, rejoin when reconnect
     */
    public void registerUsername() {
        boolean isLoggedIn = prefHelper.readBoolean(PREF_LOGGED_IN, false);
        String username = prefHelper.readString(PREF_SESSION_NAME, null);
        if (username != null && isLoggedIn) mSocket.emit(TOPIC_ADD_USER, username);
    }

    /**
     * emit event typing to socket
     * event typing is "typing", stop typing is "stop typing"
     *
     * @param isTyping {@code true} when on typing
     */
    public void onEmitTyping(boolean isTyping) {
        SocketService.getInstance()
                .getSocket()
                .emit(isTyping ? TOPIC_TYPING : TOPIC_STOP_TYPING);
    }

    /**
     * remove all message subscription and remove from listen socket
     */
    public void unSubscribeMessageEvent() {
        if (listSubscription.isEmpty()) return;
        for (int i = 0; i < listSubscription.size(); i++) {
            AppLogger.d("SOCKET:: remove event : " + listSubscription.get(i));
            mSocket.off(listSubscription.get(i));
        }
        listSubscription.clear();
    }

    @Override
    public void onDestroy() {
        AppLogger.d("SOCKET:: Service STOPPED");
        unSubscribeMessageEvent();
        compositeDisposable.dispose();
        compositeDisposable.clear();
        connected = false;
        mInstance = null;
        super.onDestroy();
    }
}
