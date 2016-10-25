package com.ittianyu.mobileguard.activity.base;

import android.view.View;
import android.widget.EditText;

import com.ittianyu.mobileguard.R;

/**
 * a input password activity
 */
public abstract class InputPasswordActivity extends BaseActivityUpEnable {
    private EditText etPassword;

    /**
     * construct method. set the action bar title
     */
    public InputPasswordActivity() {
        super(R.string.title_input_password);
    }

    /**
     * 1
     */
    @Override
    protected void initView() {
        setContentView(R.layout.activity_input_password);
        // bind view
        etPassword = (EditText) findViewById(R.id.et_password);

    }

    /**
     * 2
     */
    @Override
    protected void initData() {

    }

    /**
     * 3
     */
    @Override
    protected void initEvent() {
        // set on click listener
        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOk(etPassword.getText().toString().trim());
            }
        });

    }

    /**
     * on click ok, check password
     * @param password the EditText text
     */
    protected abstract void onOk(String password);
}
