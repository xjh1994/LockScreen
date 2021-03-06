package com.xjh1994.lockscreen.service;


import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.xjh1994.lockscreen.unlock.LockScreenActivity;

/**
 * 监听锁屏状态的服务
 *
 * @author zihao
 */
public class NsLockService extends Service {

    private static String TAG = "NsLockService";
    private Intent zdLockIntent = null;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public void onCreate() {
        super.onCreate();

        zdLockIntent = new Intent(NsLockService.this, LockScreenActivity.class);
        zdLockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		/* 注册广播 */
        IntentFilter mScreenOnFilter = new IntentFilter(
                Intent.ACTION_SCREEN_ON);
        NsLockService.this.registerReceiver(mScreenOnReceiver, mScreenOnFilter);

		/* 注册广播 */
        IntentFilter mScreenOffFilter = new IntentFilter(
                Intent.ACTION_SCREEN_OFF);
        NsLockService.this.registerReceiver(mScreenOffReceiver,
                mScreenOffFilter);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onDestroy");
        return Service.START_STICKY;

    }

    public void onDestroy() {
        super.onDestroy();
        NsLockService.this.unregisterReceiver(mScreenOnReceiver);
        NsLockService.this.unregisterReceiver(mScreenOffReceiver);
        Log.i(TAG, "onDestroy");
        // 在此重新启动
        startService(new Intent(NsLockService.this, NsLockService.class));
    }

    private KeyguardManager mKeyguardManager = null;
    private KeyguardManager.KeyguardLock mKeyguardLock = null;
    // 屏幕变亮的广播,我们要隐藏默认的锁屏界面
    private BroadcastReceiver mScreenOnReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                mKeyguardManager = (KeyguardManager) context
                        .getSystemService(Context.KEYGUARD_SERVICE);
                mKeyguardLock = mKeyguardManager.newKeyguardLock("");
                mKeyguardLock.disableKeyguard();
            }
        }

    };

    // 屏幕变暗的广播 ， 我们要调用KeyguardManager类相应方法去解除屏幕锁定
    private BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                startActivity(zdLockIntent);
            }
        }
    };
}
