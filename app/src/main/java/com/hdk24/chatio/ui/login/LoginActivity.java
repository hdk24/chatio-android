package com.hdk24.chatio.ui.login;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import androidx.viewbinding.ViewBinding;

import com.hdk24.chatio.R;
import com.hdk24.chatio.data.event.StatusEvent;
import com.hdk24.chatio.databinding.ActivityLoginBinding;
import com.hdk24.chatio.service.SocketService;
import com.hdk24.chatio.ui.base.BaseActivity;
import com.hdk24.chatio.ui.widget.TextChangedListener;
import com.hdk24.chatio.utils.NavigationUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.hdk24.chatio.utils.AppConstant.PREF_LOGGED_IN;
import static com.hdk24.chatio.utils.AppConstant.PREF_REMEMBER_ME;
import static com.hdk24.chatio.utils.AppConstant.PREF_SESSION_NAME;
import static com.hdk24.chatio.utils.AppConstant.STATUS_CONNECTED;
import static com.hdk24.chatio.utils.AppConstant.STATUS_CONNECTING;
import static com.hdk24.chatio.utils.AppConstant.STATUS_DISCONNECTED;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private EventBus bus = EventBus.getDefault();

    /**
     * init layout with view binding
     *
     * @return view binding
     */
    @Override
    protected ViewBinding bindView() {
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
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
        initViews();
    }

    /**
     * init views and handle that behavior
     */
    private void initViews() {
        String username = prefHelper.readString(PREF_SESSION_NAME, null);
        boolean rememberMe = prefHelper.readBoolean(PREF_REMEMBER_ME, false);

        // handle initial form
        if (rememberMe && username != null) {
            binding.formName.setText(username);
            binding.btnLogin.setEnabled(true);
            binding.formName.setSelection(username.length());
        }

        // handle checkbox last loggedin
        binding.lastNameView.setChecked(rememberMe);
        binding.lastNameView.setVisibility(username != null ? View.VISIBLE : View.GONE);
        binding.lastNameView.setOnCheckedChangeListener((v, b) -> {
            prefHelper.saveToBoolean(PREF_REMEMBER_ME, b);
            if (b) binding.formName.setText(username);
        });

        // handle form on typing
        binding.formName.addTextChangedListener(new TextChangedListener(binding.formName) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                binding.btnLogin.setEnabled(!s.toString().isEmpty());
            }
        });

        // handle action login
        binding.btnLogin.setOnClickListener(v -> {
            if (binding.formName.getText() == null) return;
            String formValue = binding.formName.getText().toString();
            prefHelper.saveToBoolean(PREF_LOGGED_IN, true);
            prefHelper.saveToString(PREF_SESSION_NAME, formValue);
            NavigationUtils.goToHome(this);
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
                binding.btnLogin.setEnabled(true);
                binding.btnLogin.setText(getString(R.string.action_login));
                break;
            case STATUS_CONNECTING:
                binding.btnLogin.setEnabled(false);
                binding.btnLogin.setText(getString(R.string.action_connecting));
                break;

            case STATUS_DISCONNECTED:
                binding.btnLogin.setEnabled(false);
                binding.btnLogin.setText(getString(R.string.message_connection_failed));
                break;
        }
    }

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
