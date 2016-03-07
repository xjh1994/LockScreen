package com.xjh1994.lockscreen.receiver;


import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xjh1994.lockscreen.service.NsLockService;

/**
 * 开机广播
 *
 * @author zihao
 *
 */
public class BootCompletedReceiver extends BroadcastReceiver {
	private KeyguardManager mKeyguardManager = null;
	private KeyguardManager.KeyguardLock mKeyguardLock = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		mKeyguardManager = (KeyguardManager) context
				.getSystemService(Context.KEYGUARD_SERVICE);
		mKeyguardLock = mKeyguardManager.newKeyguardLock("");
		mKeyguardLock.disableKeyguard();

		// 开机启动锁屏监测服务
		intent = new Intent(context, NsLockService.class);
		context.startService(intent);
	}
}