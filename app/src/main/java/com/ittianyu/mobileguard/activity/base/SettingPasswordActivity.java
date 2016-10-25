package com.ittianyu.mobileguard.activity.base;

import android.view.View;
import android.widget.EditText;

import com.ittianyu.mobileguard.R;

/**
 * a setting password activity
 */
public abstract class SettingPasswordActivity extends BaseActivityUpEnable {
    private EditText etPassword;
    private EditText etRepassword;

    /**
     * construct method. set the action bar title
     */
    public SettingPasswordActivity() {
        super(R.string.title_setting_password);
    }


    /**
     * 1
     */
    @Override
    protected void initView() {
        setContentView(R.layout.activity_setting_password);
        // bind view
        etPassword = (EditText) findViewById(R.id.et_password);
        etRepassword = (EditText) findViewById(R.id.et_repassword);

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
        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOk(etPassword.getText().toString().trim(), etRepassword.getText().toString().trim());
            }
        });
    }

    /**
     * on click ok, check password and setting it to config
     * @param password the input password text
     * @param repassword the input repassword text
     */
    protected abstract void onOk(String password, String repassword);
}
