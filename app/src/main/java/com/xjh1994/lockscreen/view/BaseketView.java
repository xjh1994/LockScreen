package com.xjh1994.lockscreen.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.xjh1994.lockscreen.R;
import com.xjh1994.lockscreen.unlock.MainActivity;

/**
 * Created by XJH on 16/3/7.
 */
public class BaseketView extends RelativeLayout {
    /**
     * 锁屏View
     */

    private Context context;
    private int mScreenWidth = 0;// 屏幕宽度
    private int startX = 0;     //按下点横坐标
    private int startY = 0;     //按下点纵坐标
    private int scrollX = 0;    //X轴移动距离
    private int scrollY = 0;    //Y轴移动距离

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
        // 获取屏幕分辨率
        WindowManager wm = (WindowManager) (context
                .getSystemService(Context.WINDOW_SERVICE));
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        // 这里一定要设置成透明背景,不然会无法看到底层布局
        this.setBackgroundColor(R.color.transparency);


    }

    int count = 0;
    long firClick = 0l;
    long secClick = 0l;
    private static Handler mainHandler = null; // 与主Activity通信的Handler对象

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getX();
                startY = (int) event.getY();
                Log.d("startXACTION_DOWN", String.valueOf(startX));
                Log.d("startYACTION_DOWN", String.valueOf(startY));

                count++;
                if(count == 1){
                    firClick = System.currentTimeMillis();

                } else if (count == 2){
                    secClick = System.currentTimeMillis();
                    if(secClick - firClick < 1000){
                        //双击事件
//                        mainHandler = new Handler();
                        mainHandler.obtainMessage(MainActivity.MSG_LOCK_SUCESS)
                                .sendToTarget();
                        this.setVisibility(View.GONE);
                    }
                    count = 0;
                    firClick = 0;
                    secClick = 0;

                }
                break;
            case MotionEvent.ACTION_MOVE:
                scrollX = (int) event.getX() - startX;
                scrollY = (int) event.getY() - startY;
                Log.d("startXACTION_MOVE", String.valueOf(startX));
                Log.d("startYACTION_MOVE", String.valueOf(startY));

                break;
            case MotionEvent.ACTION_UP:
                scrollX = (int) event.getX() - startX;
                scrollY = (int) event.getY() - startY;
                Log.d("startXACTION_UP", String.valueOf(startX));
                Log.d("startYACTION_UP", String.valueOf(startY));

                break;
        }
        return super.onTouchEvent(event);
    }
}
