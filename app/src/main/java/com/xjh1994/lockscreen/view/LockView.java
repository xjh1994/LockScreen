package com.xjh1994.lockscreen.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.xjh1994.lockscreen.R;
import com.xjh1994.lockscreen.unlock.MainActivity;

/**
 * Created by xjh1994 on 2016/4/6.
 */
public class LockView extends RelativeLayout {

    private Context context;
    private int mScreenWidth = 0;// 屏幕宽度

    public LockView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public LockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public LockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LockView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init();
    }

    private void init() {
        setBackground();
    }

    private static Handler mainHandler = null; // 与主Activity通信的Handler对象

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mainHandler = ((MainActivity) context).mHandler;
                mainHandler.obtainMessage(MainActivity.MSG_LOCK_SUCESS)
                        .sendToTarget();
                this.setVisibility(View.GONE);
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    private void setBackground() {
        WindowManager wm = (WindowManager) (context
                .getSystemService(Context.WINDOW_SERVICE));
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        // 这里一定要设置成透明背景,不然会无法看到底层布局
        this.setBackgroundColor(getResources().getColor(R.color.transparency));
    }

}
