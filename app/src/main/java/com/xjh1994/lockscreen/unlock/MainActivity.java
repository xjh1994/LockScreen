package com.xjh1994.lockscreen.unlock;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xjh1994.lockscreen.R;
import com.xjh1994.lockscreen.service.NsLockService;
import com.xjh1994.lockscreen.utils.MyDateUtil;
import com.xjh1994.lockscreen.view.LockLayer;

import java.util.Date;

/**
 * 锁屏界面
 *
 * @author zihao
 */
public class MainActivity extends Activity {

    private static String TAG = "MainActivity";
    private TextView tvTime, tvDate;// 显示日期的TextView
    private ImageView ivHint;// 下方的动态图片
    private AnimationDrawable animArrowDrawable = null;
    public static int MSG_LOCK_SUCESS = 0x123;// 解锁成功
    public static int UPDATE_TIME = 0x234;// 更新时间
    private boolean isTime = true;// 是否允许更新时间
    private LockLayer lockLayer;
    private View lockView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lockView = View.inflate(this, R.layout.main, null);
        lockLayer = new LockLayer(this);
        lockLayer.setLockView(lockView);// 设置要展示的页面
        lockLayer.lock();// 开启锁屏

        initView();
        startService(new Intent(MainActivity.this, NsLockService.class));

        disableSystemLock();
    }

    /**
     * 屏蔽系统锁屏
     */
    private void disableSystemLock() {
        KeyguardManager mKeyguardManager = null;
        KeyguardManager.KeyguardLock mKeyguardLock = null;
        mKeyguardManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        mKeyguardLock = mKeyguardManager.newKeyguardLock("");
        mKeyguardLock.disableKeyguard();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        tvTime = (TextView) lockView.findViewById(R.id.tv_time);
        tvDate = (TextView) lockView.findViewById(R.id.tv_date);
        ivHint = (ImageView) lockView.findViewById(R.id.iv_hint);
        ivHint.setImageResource(R.anim.slider_tip_anim);
        animArrowDrawable = (AnimationDrawable) ivHint.getDrawable();
        animArrowDrawable.start();
        getNewTime();
    }

    /**
     * 消息处理
     */
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (MSG_LOCK_SUCESS == msg.what) {
                lockLayer.unlock();
                finish(); // 锁屏成功时，结束我们的Activity界面
            }
            if (UPDATE_TIME == msg.what) {// 更新时间
                Date date = (Date) msg.obj;
                // 设置当前时间
                tvTime.setText(MyDateUtil.getChangeTimeFormat(date));
                // 设置日期
                tvDate.setText(MyDateUtil.getChangeDateFormat(date) + "\t"
                        + MyDateUtil.getChangeWeekFormat(date));
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
}