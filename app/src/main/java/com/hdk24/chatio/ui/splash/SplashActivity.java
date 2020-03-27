package com.hdk24.chatio.ui.splash;

import android.os.Bundle;

import androidx.viewbinding.ViewBinding;

import com.hdk24.chatio.R;
import com.hdk24.chatio.data.event.StatusEvent;
import com.hdk24.chatio.databinding.ActivitySplashBinding;
import com.hdk24.chatio.service.SocketService;
import com.hdk24.chatio.ui.base.BaseActivity;
import com.hdk24.chatio.utils.NavigationUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.hdk24.chatio.utils.AppConstant.PREF_LOGGED_IN;
import static com.hdk24.chatio.utils.AppConstant.PREF_SESSION_NAME;
import static com.hdk24.chatio.utils.AppConstant.STATUS_CONNECTED;
import static com.hdk24.chatio.utils.AppConstant.STATUS_CONNECTING;
import static com.hdk24.chatio.utils.AppConstant.STATUS_DISCONNECTED;

/*
 *  Created by Hanantadk on 24/03/20.
 *  Copyright (c) 2020. All rights reserved.
 *  Last modified 24/03/20.
 */
public class SplashActivity extends BaseActivity {

    private EventBus bus = EventBus.getDefault();
    private ActivitySplashBinding binding;
    private boolean isNext;
    private boolean isLoggedIn;
    private String mUsername;

    /**
     * init layout with view binding
     *
     * @return view binding
     */
    @Override
    protected ViewBinding bindView() {
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        return binding;
    }

    /**
     * handle view when view is ready
     *
     * @param savedInstanceState bundle
     */
    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        bus.register(this);

        startSocketService();
        binding.containerSplash.post(this::initViews);
    }

    /**
     * init views and handle that behavior
     */
    private void initViews() {
        mUsername = prefHelper.readString(PREF_SESSION_NAME, null);
        isLoggedIn = prefHelper.readBoolean(PREF_LOGGED_IN, false);

        // handle view base socket connection
        if (SocketService.getInstance() != null && SocketService.getInstance().getSocket().connected()) {
            binding.btnStarted.setEnabled(true);
            binding.btnStarted.setText(isLoggedIn ? "Login as " + mUsername : getString(R.string.action_started));
        } else {
            binding.btnStarted.setEnabled(false);
            binding.btnStarted.setText(getString(R.string.action_connecting));
        }

        binding.btnStarted.setOnClickListener(v -> {
            isNext = true;
            if (isLoggedIn) NavigationUtils.goToHome(this);
            else NavigationUtils.goToLoggedIn(this);
            finish();
        });
    }

    /**
     * subscribe status event from socket in main thread
     * handle socket connection
     *
     * @param event status
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStatusEvent(StatusEvent event) {
        switch (event.getStatus()) {
            case STATUS_CONNECTED:
                binding.btnStarted.setEnabled(true);
                binding.btnStarted.setText(getString(R.string.action_started));
                binding.btnStarted.setText(isLoggedIn ? "Login as " + mUsername : getString(R.string.action_started));
                break;
            case STATUS_CONNECTING:
                binding.btnStarted.setEnabled(false);
                binding.btnStarted.setText(getString(R.string.action_connecting));
                break;

            case STATUS_DISCONNECTED:
                binding.btnStarted.setEnabled(false);
                binding.btnStarted.setText(getString(R.string.message_connection_failed));
                break;
        }
    }

    /**
     * stop service when user close app from splash screen
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
        if (!isNext) SocketService.getInstance().stopSelf();
    }
}
