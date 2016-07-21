package com.xjh1994.lockscreen.unlock;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.xjh1994.lockscreen.R;
import com.xjh1994.lockscreen.utils.DisplayUtils;

import java.util.Date;

/**
 * 锁屏界面
 *
 * @author zihao
 */
public class LockScreenActivity extends Activity {
    private static String TAG = "LockScreenActivity";

    private static final int MSG_LAUNCH_HOME = 0;
    public static int MSG_LOCK_SUCESS = 0x123;// 解锁成功
    public static int UPDATE_TIME = 0x234;// 更新时间
    private boolean isTime = true;// 是否允许更新时间
    private Handler mainHandler;
    private float mStartX;
    private ImageView mMoveView;
    private int mWidth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        setContentView(R.layout.activity_lockscreen);

        initView();
        initWidth();
        initMoveView();
        initHandler();
    }

    private void initWidth() {
        mWidth = DisplayUtils.getScreenWidth(this);
    }

    private void initMoveView() {
        mMoveView = (ImageView) findViewById(R.id.iv_image);
    }

    private void initHandler() {
        mainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_LAUNCH_HOME:
                        finish();
                        break;
                }
            }
        };
    }

    /**
     * 初始化视图
     */
    private void initView() {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float nx = event.getX();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartX = nx;
                onAnimationEnd();
            case MotionEvent.ACTION_MOVE:
                handleMoveView(nx);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                doTriggerEvent(nx);
                break;
        }
        return true;
    }

    private void onAnimationEnd() {

    }

    private void handleMoveView(float x) {
        float movex = x - mStartX;
        if (movex < 0)
            movex = 0;
        mMoveView.setTranslationX(movex);
        float mWidthFloat = (float) mWidth;//屏幕显示宽度
        if (getBackground() != null) {
            getBackground().setAlpha((int) ((mWidthFloat - mMoveView.getTranslationX()) / mWidthFloat * 200));//初始透明度的值为200
        }
    }

    private View getBackground() {
        return getWindow().getDecorView();
    }

    private void doTriggerEvent(float x) {
        float movex = x - mStartX;
        if (movex > (mWidth * 0.4)) {
            moveMoveView(mWidth - mMoveView.getLeft(), true);//自动移动到屏幕右边界之外，并finish掉

        } else {
            moveMoveView(-mMoveView.getLeft(), false);//自动移动回初始位置，重新覆盖
        }
    }

    private void moveMoveView(float to, boolean exit) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mMoveView, "translationX", to);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (getBackground() != null) {
                    getBackground().setAlpha((int) (((float) mWidth - mMoveView.getTranslationX()) / (float) mWidth * 200));
                }
            }
        });//随移动动画更新背景透明度
        animator.setDuration(250).start();
        if (exit) {
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mainHandler.obtainMessage(LockScreenActivity.MSG_LAUNCH_HOME).sendToTarget();
                    super.onAnimationEnd(animation);
                }
            });
        }//监听动画结束，利用Handler通知Activity退出
    }

    /**
     * 消息处理
     */
    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (MSG_LOCK_SUCESS == msg.what) {
//                lockLayer.unlock();
                finish(); // 锁屏成功时，结束我们的Activity界面
            }
            if (UPDATE_TIME == msg.what) {// 更新时间
                Date date = (Date) msg.obj;
                // 设置当前时间
//                tvTime.setText(MyDateUtil.getChangeTimeFormat(date));
                // 设置日期
//                tvDate.setText(MyDateUtil.getChangeDateFormat(date) + "\t"
//                        + MyDateUtil.getChangeWeekFormat(date));
            }
        }
    };

    /**
     * 每隔一分钟就获取新的时间
     */

    private void getNewTime() {
        new Thread() {
            public void run() {
                while (isTime) {
                    Date date = new Date();
                    Message msg = new Message();
                    msg.obj = date;
                    msg.what = UPDATE_TIME;
                    mHandler.sendMessage(msg);
                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            ;
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        isTime = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        isTime = false;
    }

    protected void onDestory() {
        super.onDestroy();
        Log.i(TAG, "onDestory");
        isTime = false;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int key = event.getKeyCode();
        switch (key) {
            case KeyEvent.KEYCODE_BACK: {
                return true;
            }
            case KeyEvent.KEYCODE_MENU: {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}