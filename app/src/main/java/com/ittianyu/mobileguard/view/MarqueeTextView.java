package com.ittianyu.mobileguard.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * An auto rolling TextView
 */
public class MarqueeTextView extends TextView {

    public MarqueeTextView(Context context) {
        super(context);
        initAttrs();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs();
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs();
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs();
    }

    /**
     * init attrs of TextView
     */
    private void initAttrs() {
        this.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        this.setSingleLine(true);// only this can be useful
//        this.setLines(1); // can't get the same effect to setSingleLine
//        this.setSelected(true);
//        this.setFocusable(true);
//        this.setFocusableInTouchMode(true);
    }

    /**
     * always return true to make it auto rolling
     *
     * @return true
     */
    @Override
    public boolean isFocused() {
        return true;
    }
}
