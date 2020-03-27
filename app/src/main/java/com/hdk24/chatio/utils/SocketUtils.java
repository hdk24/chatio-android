package com.hdk24.chatio.utils;

import io.reactivex.Observable;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/*
 *  Created by Hanantadk on 26/03/20.
 *  Copyright (c) 2020. All rights reserved.
 *  Last modified 26/03/20.
 */
public final class SocketUtils {

    public static Observable<String> makeSubscription(final Socket socket, final String topic) {
        return Observable.create(subscriber -> {
            final Emitter.Listener listener = args -> subscriber.onNext(args != null && args.length > 0 ? args[0].toString() : null);
            socket.on(topic, listener);
        });
    }
}
