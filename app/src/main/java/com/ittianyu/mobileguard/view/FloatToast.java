package com.ittianyu.mobileguard.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.ittianyu.mobileguard.constant.Constant;
import com.ittianyu.mobileguard.utils.ConfigUtils;

/**
 * Created by yu.
 * Like a Toast, show in front of window and can be dragged.
 * You can put any view in it.
 */
public class FloatToast {
    private WindowManager mWM;
    private View mView;
    private View mNextView;
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();

    private float startX, startY;

    /**
     * Construct an empty  FloatToast object.
     */
    private FloatToast(Context context) {
        // XXX This should be changed to use a Dialog, with a Theme.Toast
        // defined that sets up the layout params appropriately.
        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
//        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        // set gravity
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
    }

    /**
     * Make a standard toast that contains a custom view.
     * @param context
     * @param view
     * @return
     */
    public static FloatToast makeView(final Context context, View view) {
        final FloatToast toast = new FloatToast(context);
        // record view
        toast.mNextView = view;

        // set touch listener. Implement drag effect
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        // if action down, record the press position
                        toast.startX = event.getRawX();
                        toast.startY = event.getRawY();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        // move , record current position
                        float endX = event.getRawX();
                        float endY = event.getRawY();
                        // move toast
                        toast.mParams.x += (int)(endX - toast.startX);
                        toast.mParams.y += (int)(endY - toast.startY);
                        // update start position
                        toast.startX = endX;
                        toast.startY = endY;

                        // update view
                        toast.mWM.updateViewLayout(toast.mView, toast.mParams);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        // get screen size
                        Point point = new Point();
                        toast.mWM.getDefaultDisplay().getSize(point);
                        // prevent toast.mParams.x y beyond the screen
                        if(toast.mParams.x < 0) {
                            toast.mParams.x = 0;
                        } else if (toast.mParams.x + toast.mView.getWidth() > point.x) {
                            toast.mParams.x = point.x - toast.mView.getWidth();
                        }
                        if(toast.mParams.y < 0) {
                            toast.mParams.y = 0;
                        } else if (toast.mParams.y + toast.mView.getHeight() > point.y) {
                            toast.mParams.y = point.y - toast.mView.getHeight();
                        }
                        // record x y
                        ConfigUtils.putInt(context, Constant.KEY_FLOAT_TOAST_X, toast.mParams.x);
                        ConfigUtils.putInt(context, Constant.KEY_FLOAT_TOAST_Y, toast.mParams.y);

                        break;
                    }

                }
                return true;
            }
        });

        return toast;
    }

    /**
     * you can use this method to get the view to change content
     * @return the view which was set on makeView
     */
    public View getView() {
        return mNextView;
    }

    /**
     * Show the view in front of screen at last position until call close()
     * If the first show, it will show in center of screen.
     * You also can call show(x, y) to specific position.
     */
    public void show() {
        if (mView != mNextView) {
            // remove the old view if necessary
            close();
            mView = mNextView;
            Context context = mView.getContext().getApplicationContext();
            if (context == null) {
                context = mView.getContext();
            }
            mWM = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            if (mView.getParent() != null) {
                mWM.removeView(mView);
            }
            // get device width and height
            Point point = new Point();
            mWM.getDefaultDisplay().getSize(point);
            // get config x,y
            mParams.x = ConfigUtils.getInt(context, Constant.KEY_FLOAT_TOAST_X, (point.x - mView.getWidth()) / 2);
            mParams.y = ConfigUtils.getInt(context, Constant.KEY_FLOAT_TOAST_Y, (point.y - mView.getHeight()) / 2);
//            System.out.println("config (x, y) = " + mParams.x + "," + mParams.y);

            mWM.addView(mView, mParams);
        }
    }

    /**
     * Show the view in front of screen at position x,y until call close()
     * @param x the position x of view
     * @param y the position y of view
     */
    public void show(int x, int y) {
        mParams.x = x;
        mParams.y = y;
        if (mView != mNextView) {
            // remove the old view if necessary
            close();
            mView = mNextView;
            Context context = mView.getContext().getApplicationContext();
            if (context == null) {
                context = mView.getContext();
            }
            mWM = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            if (mView.getParent() != null) {
                mWM.removeView(mView);
            }
            mWM.addView(mView, mParams);
        }
    }

    /**
     * hide the float toast and release resources
     */
    public void close() {
        if (mView != null) {
            // note: checking parent() just to make sure the view has
            // been added...  i have seen cases where we get here when
            // the view isn't yet added, so let's try not to crash.
            if (mView.getParent() != null) {
                mWM.removeView(mView);
            }
            mView = null;
        }
    }

}

/*

     * Make a standard toast that just contains a text view.
     *
     * @param context  The context to use.  Usually your {@link android.app.Application}
     *                 or {@link android.app.Activity} object.
     * @param text     The text to show.  Can be formatted text.
     * @param duration How long to display the message.  Either {@link #LENGTH_SHORT} or
     *                 {@link #LENGTH_LONG}
    public static Toast makeText(Context context, CharSequence text, @Duration int duration) {
        Toast result = new Toast(context);

        LayoutInflater inflate = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(com.android.internal.R.layout.transient_notification, null);
        TextView tv = (TextView)v.findViewById(com.android.internal.R.id.message);
        tv.setText(text);

        result.mNextView = v;
        result.mDuration = duration;

        return result;
    }


    * Show the view for the specified duration.
    public void show() {
        if (mNextView == null) {
            throw new RuntimeException("setView must have been called");
        }

        INotificationManager service = getService();
        String pkg = mContext.getOpPackageName();
        TN tn = mTN;
        tn.mNextView = mNextView;

        try {
            service.enqueueToast(pkg, tn, mDuration);
        } catch (RemoteException e) {
            // Empty
        }
    }


    // TN 类继承了ITransientNotification.Stub，说明这个类提供了显示Toast的方法。
    // TN类主要的代码

    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    final Handler mHandler = new Handler();


    WindowManager mWM;

    TN() {
        // XXX This should be changed to use a Dialog, with a Theme.Toast
        // defined that sets up the layout params appropriately.
        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.windowAnimations = com.android.internal.R.style.Animation_Toast;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
    }

    public void handleShow() {
        if (localLOGV) Log.v(TAG, "HANDLE SHOW: " + this + " mView=" + mView
                + " mNextView=" + mNextView);
        if (mView != mNextView) {
            // remove the old view if necessary
            handleHide();
            mView = mNextView;
            Context context = mView.getContext().getApplicationContext();
            String packageName = mView.getContext().getOpPackageName();
            if (context == null) {
                context = mView.getContext();
            }
            mWM = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            // We can resolve the Gravity here by using the Locale for getting
            // the layout direction
            final Configuration config = mView.getContext().getResources().getConfiguration();
            final int gravity = Gravity.getAbsoluteGravity(mGravity, config.getLayoutDirection());
            mParams.gravity = gravity;
            if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.FILL_HORIZONTAL) {
                mParams.horizontalWeight = 1.0f;
            }
            if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.FILL_VERTICAL) {
                mParams.verticalWeight = 1.0f;
            }
            mParams.x = mX;
            mParams.y = mY;
            mParams.verticalMargin = mVerticalMargin;
            mParams.horizontalMargin = mHorizontalMargin;
            mParams.packageName = packageName;
            if (mView.getParent() != null) {
                if (localLOGV) Log.v(TAG, "REMOVE! " + mView + " in " + this);
                mWM.removeView(mView);
            }
            if (localLOGV) Log.v(TAG, "ADD! " + mView + " in " + this);
            mWM.addView(mView, mParams);
            trySendAccessibilityEvent();
        }
    }

    public void handleHide() {
        if (localLOGV) Log.v(TAG, "HANDLE HIDE: " + this + " mView=" + mView);
        if (mView != null) {
            // note: checking parent() just to make sure the view has
            // been added...  i have seen cases where we get here when
            // the view isn't yet added, so let's try not to crash.
            if (mView.getParent() != null) {
                if (localLOGV) Log.v(TAG, "REMOVE! " + mView + " in " + this);
                mWM.removeView(mView);
            }

            mView = null;
        }
    }


     * schedule handleShow into the right thread

    @Override
    public void show() {
        if (localLOGV) Log.v(TAG, "SHOW: " + this);
        mHandler.post(mShow);
    }


    * schedule handleHide into the right thread

    @Override
    public void hide() {
        if (localLOGV) Log.v(TAG, "HIDE: " + this);
        mHandler.post(mHide);
    }

*/
