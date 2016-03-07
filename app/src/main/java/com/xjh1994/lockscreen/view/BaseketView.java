package com.xjh1994.lockscreen.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by XJH on 16/3/7.
 */
public class BaseketView extends RelativeLayout {
    /**
     * 锁屏View
     * @param context
     */

    private Context context;

    public BaseketView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public BaseketView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
