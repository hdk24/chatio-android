package com.hdk24.chatio.ui.home;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewbinding.ViewBinding;

import com.hdk24.chatio.R;
import com.hdk24.chatio.data.event.StatusEvent;
import com.hdk24.chatio.databinding.ActivityHomeBinding;
import com.hdk24.chatio.service.SocketService;
import com.hdk24.chatio.ui.adapter.HomeAdapter;
import com.hdk24.chatio.ui.base.BaseActivity;
import com.hdk24.chatio.utils.DataUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

import static com.hdk24.chatio.utils.AppConstant.PREF_SESSION_NAME;
import static com.hdk24.chatio.utils.AppConstant.STATUS_CONNECTED;
import static com.hdk24.chatio.utils.AppConstant.STATUS_CONNECTING;
import static com.hdk24.chatio.utils.AppConstant.STATUS_DISCONNECTED;

public class HomeActivity extends BaseActivity {

    private ActivityHomeBinding binding;
    private EventBus bus = EventBus.getDefault();
    private HomeAdapter mAdapter;
    private String username;

    /**
     * init layout with view binding
     *
     * @return view binding
     */
    @Override
    protected ViewBinding bindView() {
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
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
        username = prefHelper.readString(PREF_SESSION_NAME, null);
        startMessageService();
        initViews();
    }

    /**
     * init views and handle that behavior
     */
    private void initViews() {
        binding.initialName.setText(username.substring(0, 1).toUpperCase(Locale.getDefault()));
        binding.textName.setText(getString(R.string.label_login_as, username));

        binding.listMessageView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new HomeAdapter(this, DataUtils.defaultMessage());
        binding.listMessageView.setAdapter(mAdapter);
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
                binding.textName.setText(getString(R.string.label_login_as, username));
                break;
            case STATUS_CONNECTING:
                binding.textName.setText(getString(R.string.action_connecting));
                break;

            case STATUS_DISCONNECTED:
                binding.textName.setText(getString(R.string.message_connection_failed));
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
        SocketService.getInstance().stopSelf();
    }
}
