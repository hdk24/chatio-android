package com.hdk24.chatio.ui.widget;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/*
 *  Created by Hanantadk on 27/03/20.
 *  Copyright (c) 2020. All rights reserved.
 *  Last modified 27/03/20.
 */
public abstract class TextChangedListener implements TextWatcher {
    private EditText target;

    public TextChangedListener(EditText target) {
        this.target = target;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        this.onTextChanged(target, s);
    }

    public abstract void onTextChanged(EditText target, Editable s);
}