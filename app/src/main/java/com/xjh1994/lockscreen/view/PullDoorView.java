package com.xjh1994.lockscreen.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.xjh1994.lockscreen.R;
import com.xjh1994.lockscreen.unlock.LockScreenActivity;

/**
 * 锁屏界面
 *
 * @author zihao
 *
 */
public class PullDoorView extends RelativeLayout {

	private Context mContext;
	private Scroller mScroller;
	private int mScreenWidth = 0;// 屏幕宽度
	private int mLastDownX = 0;// 按下时X轴的位置
	private int mCurryX;// X轴移动的距离
	private int mDelX;// X轴移动的距离
	private SoundPool soundPool;// 声明一个SoundPool,SoundPool最大只能申请1M的内存空间
	private int musicId;// 定义一个整型用load（）；来设置suondID
	private boolean mCloseFlag = false;// 标识是否解锁
	private static Handler mainHandler = null; // 与主Activity通信的Handler对象
	private ImageView mImgView;

	/**
	 * 构造方法
	 *
	 * @param context
	 */
	public PullDoorView(Context context) {
		super(context);
		mContext = context;
		initView();
	}

	/**
	 * 构造方法
	 *
	 * @param context
	 * @param attrs
	 */
	public PullDoorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}

	/**
	 * 初始化锁屏视图
	 */
	@SuppressLint("ResourceAsColor")
	private void initView() {
		// 这个Interpolator你可以设置别的 我这里选择的是有弹跳效果的Interpolator
		Interpolator polator = new BounceInterpolator();
		mScroller = new Scroller(mContext, polator);

		// 获取屏幕分辨率
		WindowManager wm = (WindowManager) (mContext
				.getSystemService(Context.WINDOW_SERVICE));
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;

		// 这里你一定要设置成透明背景,不然会影响你看到底层布局
		this.setBackgroundColor(R.color.transparency);
		mImgView = new ImageView(mContext);
		mImgView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		mImgView.setScaleType(ImageView.ScaleType.FIT_XY);// 填充整个屏幕
		mImgView.setImageResource(R.drawable.bg); // 默认背景
		addView(mImgView);
		// 设置声音
		soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);// 第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
		musicId = soundPool.load(mContext, R.raw.music, 1); // 把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
	}

	/**
	 * 设置推动门背景
	 *
	 * @param id
	 */
	public void setBgImage(int id) {
		mImgView.setImageResource(id);
	}

	/**
	 * 设置推动门背景
	 *
	 * @param drawable
	 */
	public void setBgImage(Drawable drawable) {
		mImgView.setImageDrawable(drawable);
	}

	/**
	 * 解锁推门动画
	 *
	 * @param startX
	 *            // 起始位置的X坐标点
	 * @param dx
	 *            // 结束时的X坐标点
	 * @param duration
	 *            // 动画时长
	 */
	public void startBounceAnim(int startX, int dx, int duration) {
		mScroller.startScroll(startX, 0, dx, 0, duration);
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
			case MotionEvent.ACTION_DOWN:// 按下
				mLastDownX = (int) event.getX();
				return true;
			case MotionEvent.ACTION_MOVE:
				mCurryX = (int) event.getX();
				mDelX = mCurryX - mLastDownX;
				// 只准右滑有效
				if (mDelX > 0) {
					scrollTo(-mDelX, 0);
				}
				break;
			case MotionEvent.ACTION_UP:
				mCurryX = (int) event.getX();
				mDelX = mCurryX - mLastDownX;

				if (mDelX > 0) {// 右滑
					if (Math.abs(mDelX) > mScreenWidth / 2) {// 当滑动距离超过半屏时解锁
						// 向右滑动超过半个屏幕宽的时候 开启向右消失动画
						startBounceAnim(this.getScrollX(), -mScreenWidth, 1000);
						mCloseFlag = true;
					} else {// 向右滑动未超过半个屏幕宽的时候 开启向左弹动动画
						startBounceAnim(this.getScrollX(), -this.getScrollX(), 1200);

					}
				} else {// 回滚到原位置
					startBounceAnim(this.getScrollX(), -this.getScrollX(), 1200);
				}

				break;
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 滚动界面
	 */
	@Override
	public void computeScroll() {

		if (mScroller.computeScrollOffset()) {// 滚动界面
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			Log.i("scroller", "getCurrX()= " + mScroller.getCurrX()
					+ "     getCurrY()=" + mScroller.getCurrY()
					+ "  getFinalY() =  " + mScroller.getFinalY());
			// 更新界面
			postInvalidate();
		} else {// 解锁成功
			if (mCloseFlag) {
				soundPool.play(musicId, 1, 1, 0, 0, 1);
				mainHandler = new Handler();
				mainHandler.obtainMessage(LockScreenActivity.MSG_LOCK_SUCESS)
						.sendToTarget();
				this.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 设置Handler
	 *
	 * @param handler
	 */
	public static void setHandler(Handler handler) {
		mainHandler = handler;// activity所在的Handler对象
	}
}
