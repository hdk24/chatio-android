package com.hdk24.chatio.ui.home;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewbinding.ViewBinding;

import com.hdk24.chatio.R;
import com.hdk24.chatio.data.event.MessageEvent;
import com.hdk24.chatio.data.event.StatusEvent;
import com.hdk24.chatio.data.model.Message;
import com.hdk24.chatio.databinding.ActivityHomeBinding;
import com.hdk24.chatio.service.SocketService;
import com.hdk24.chatio.ui.adapter.HomeAdapter;
import com.hdk24.chatio.ui.base.BaseActivity;
import com.hdk24.chatio.utils.DataUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import static com.hdk24.chatio.utils.AppConstant.PREF_SESSION_NAME;
import static com.hdk24.chatio.utils.AppConstant.STATUS_CONNECTED;
import static com.hdk24.chatio.utils.AppConstant.STATUS_CONNECTING;
import static com.hdk24.chatio.utils.AppConstant.STATUS_DISCONNECTED;
import static com.hdk24.chatio.utils.AppConstant.TOPIC_MESSAGE;
import static com.hdk24.chatio.utils.AppConstant.TOPIC_STOP_TYPING;
import static com.hdk24.chatio.utils.AppConstant.TOPIC_TYPING;
import static com.hdk24.chatio.utils.AppConstant.TYPE_MESSAGE;

public class HomeActivity extends BaseActivity implements HomeAdapter.OnHomeListener {

    private ActivityHomeBinding binding;
    private EventBus bus = EventBus.getDefault();
    private HomeAdapter mAdapter;
    private String username;
    private String socketTempUsername = "";
    private String socketTempText = "";

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
        SocketService.getInstance().registerUsername();
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
        mAdapter = new HomeAdapter(this, DataUtils.defaultMessage(), this);
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
                binding.initialName.setBackgroundResource(R.drawable.bg_circle_accent);
                binding.textName.setText(getString(R.string.label_login_as, username));
                break;
            case STATUS_CONNECTING:
                binding.initialName.setBackgroundResource(R.drawable.bg_circle_yellow);
                binding.textName.setText(getString(R.string.action_connecting));
                break;

            case STATUS_DISCONNECTED:
                binding.initialName.setBackgroundResource(R.drawable.bg_circle_off);
                binding.textName.setText(getString(R.string.message_connection_failed));
                break;
        }
    }

    /**
     * subscribe message event from socket
     * handle event by topic and update home adapter
     *
     * @param event message
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String username = "";
        String text = "";

        switch (event.topic) {
            case TOPIC_MESSAGE:
                try {
                    JSONObject jsMessage = new JSONObject(event.message);
                    username = jsMessage.getString("username");
                    text = jsMessage.getString("message");
                    socketTempUsername = username;
                    socketTempText = text;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message.Builder msgBuilder = new Message.Builder(TYPE_MESSAGE);
                msgBuilder.time(event.time);
                msgBuilder.message(username + ":" + text);
                msgBuilder.isPrivate(false);
                msgBuilder.username("Socket.io");
                mAdapter.updateMessage(0, msgBuilder.build());
                break;

            case TOPIC_TYPING:
                try {
                    JSONObject jsMessage = new JSONObject(event.message);
                    username = jsMessage.getString("username");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message.Builder typingBuilder = new Message.Builder(TYPE_MESSAGE);
                typingBuilder.time(event.time);
                typingBuilder.message(username + " : is typing...");
                typingBuilder.isPrivate(false);
                typingBuilder.username("Socket.io");
                mAdapter.updateMessage(0, typingBuilder.build());
                break;

            case TOPIC_STOP_TYPING:
                Message.Builder stopBuilder = new Message.Builder(TYPE_MESSAGE);
                stopBuilder.time(event.time);
                if (socketTempUsername.isEmpty()) {
                    stopBuilder.message("let's start create message with socket.io");
                } else {
                    stopBuilder.message(socketTempUsername + " : " + socketTempText);
                }
                stopBuilder.isPrivate(false);
                stopBuilder.username("Socket.io");
                mAdapter.updateMessage(0, stopBuilder.build());
                break;
        }
    }

    @Override
    public void onItemClicked(Message message) {

    }

    /**
     * when back press from home, app will exit
     * so stop service on back press, don't do on destroy
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SocketService.getInstance().stopSelf();
    }

    /**
     * stop service when user close app from splash screen
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }
}
