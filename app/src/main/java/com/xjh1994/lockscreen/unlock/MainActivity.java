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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
public class MainActivity extends Activity implements View.OnTouchListener {

    private static String TAG = "MainActivity";
    private TextView tvTime, tvDate;// 显示日期的TextView
    private ImageView ivHint;// 下方的动态图片
    private AnimationDrawable animArrowDrawable = null;
    public static int MSG_LOCK_SUCESS = 0x123;// 解锁成功
    public static int UPDATE_TIME = 0x234;// 更新时间
    private boolean isTime = true;// 是否允许更新时间
    private LockLayer lockLayer;
    private View lockView;

    private int screenWidth;
    private int screenHeight;
    private int lastX, lastY;
    private int startX, startY;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lockView = View.inflate(this, R.layout.activity_main, null);
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

        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels - 150;

        tvTime.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;
        int dx = 0;
        int dy = 0;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Toast.makeText
                        (MainActivity.this, "Down...", Toast.LENGTH_SHORT).show();
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
//			System.out.println("lastX:"+lastX+",lastY:"+lastY);
                break;
            case MotionEvent.ACTION_MOVE:
                dx = (int) event.getRawX() - lastX;
                dy = (int) event.getRawY() - lastY;

                left = v.getLeft() + dx;
                top = v.getTop() + dy;
                right = v.getRight() + dx;
                bottom = v.getBottom() + dy;

                System.out.println("left:" + left);
                System.out.println("top:" + top);
                System.out.println("right:" + right);
                System.out.println("bottom:" + bottom);

                // 设置不能出界
                if (left < 0) {
                    left = 0;
                    right = left + v.getWidth();
                }

                if (right > screenWidth) {
                    right = screenWidth;
                    left = right - v.getWidth();
                }

                if (top < 0) {
                    top = 0;
                    bottom = top + v.getHeight();
                }

                if (bottom > screenHeight) {
                    bottom = screenHeight;
                    top = bottom - v.getHeight();
                }
                v.layout(left, top, right, bottom);

                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();

                break;
            case MotionEvent.ACTION_UP:
//                Rect rect = new Rect(left, top, right, bottom);
//                Rect rect1 = new Rect(ivHint.getLeft(), ivHint.getTop(), ivHint.getRight(), ivHint.getBottom());

                int[] location = new int[2];
                tvTime.getLocationOnScreen(location);

                int x = location[0];
                int y = location[1];

                if (x > tvDate.getLeft() && x < tvDate.getRight() && y > tvDate.getTop() && y < tvDate.getBottom()) {
                    mHandler.obtainMessage(MSG_LOCK_SUCESS).sendToTarget();
                    Toast.makeText(MainActivity.this, "yes", Toast.LENGTH_SHORT).show();
                } else {
                    int l = v.getLeft() - dx;
                    int t = v.getTop() - dy;
                    int r = v.getRight() - dx;
                    int b = v.getBottom() - dy;
                    v.layout(l, t, r, b);
                }

                /*if (isRectIntersect(rect, rect1)) {
                    mHandler.obtainMessage(MSG_LOCK_SUCESS).sendToTarget();
                    Toast.makeText(MainActivity.this, "yes", Toast.LENGTH_SHORT).show();
                } else {
                    int l = v.getLeft() - dx;
                    int t = v.getTop() - dy;
                    int r = v.getRight() - dx;
                    int b = v.getBottom() - dy;
                    v.layout(l, t, r, b);
                }*/
                break;
        }

        return true;
    }

    int ab(int n) {
        if (n >= 0) return n;
        else return -n;
    }

    public boolean isRectIntersect(Rect rect, Rect rect1) {
        return ((rect.getLeftTopX() > rect1.getLeftTopX() && rect.getRightBottomX() > rect1.getLeftTopX()) ||
                (rect.getLeftTopX() < rect1.getLeftTopX() && rect.getRightBottomX() < rect1.getLeftTopX()) ||
                (rect.getLeftTopY() > rect1.getLeftTopY() && rect.getRightBottomY() > rect1.getLeftTopY()) ||
                (rect.getLeftTopY() < rect1.getLeftTopY() && rect.getRightBottomY() < rect1.getLeftTopY()));
    }

    class Rect {
        private float x1;
        private float y1;
        private float x2;
        private float y2;

        public Rect(float x1, float y1, float x2, float y2) {
            // [Neo] 确保存储的点为 左上坐标(x1, y1) 以及 右下坐标(x2, y2)
            float tmp;
            if (x1 > x2) {
                tmp = x1;
                x1 = x2;
                x2 = tmp;
            }

            if (y1 > y2) {
                tmp = y1;
                y1 = y2;
                y2 = tmp;
            }

            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        public float getLeftTopX() {
            return x1;
        }

        public float getLeftTopY() {
            return y1;
        }

        public float getRightBottomX() {
            return x2;
        }

        public float getRightBottomY() {
            return y2;
        }

    }


    /**
     * 消息处理
     */
    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler() {
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