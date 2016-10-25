package com.ittianyu.mobileguard.activity;

import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ittianyu.mobileguard.R;
import com.ittianyu.mobileguard.activity.base.BaseActivityUpEnable;
import com.ittianyu.mobileguard.engine.PhoneLocationEngine;

/**
 * query phone number location
 */
public class PhoneLocationActivity extends BaseActivityUpEnable {
    // view
    private EditText etNumber;
    private TextView tvLocation;
    // data
    private PhoneLocationEngine engine = new PhoneLocationEngine();

    /**
     * construct method
     */
    public PhoneLocationActivity() {
        super(R.string.phone_location_query);
    }

    /**
     * 1
     */
    @Override
    protected void initView() {
        setContentView(R.layout.activity_phone_location);

        etNumber = (EditText) findViewById(R.id.et_number);
        tvLocation = (TextView) findViewById(R.id.tv_location);
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
        findViewById(R.id.btn_query_phone_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryPhoneLocation();
            }
        });
    }

    /**
     * query phone location
     */
    private void queryPhoneLocation() {
        // get input number
        String number = etNumber.getText().toString().trim();
        // check empty
        if(TextUtils.isEmpty(number)) {
            Toast.makeText(this, R.string.phone_number_can_not_be_empty, Toast.LENGTH_SHORT).show();
            // start shake animation
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            etNumber.startAnimation(shake);
            return;
        }
        // call engine to query

        String location = engine.getLocation(this, number);
        // check empty
        if(TextUtils.isEmpty(location)) {
            Toast.makeText(this, R.string.tips_failed_to_query_phone_location, Toast.LENGTH_SHORT).show();
        }
        // set text
        tvLocation.setText(location);
    }


}
