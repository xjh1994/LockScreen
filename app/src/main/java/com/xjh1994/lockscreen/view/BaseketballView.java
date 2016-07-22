package com.xjh1994.lockscreen.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import com.xjh1994.lockscreen.Event.FinishEvent;
import com.xjh1994.lockscreen.R;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xjh1994 on 2016/7/21.
 */
public class BaseketballView extends RelativeLayout {

    @Bind(R.id.iv_basketball)
    MatrixImageView ivBasketball;
    @Bind(R.id.iv_basket)
    MatrixImageView ivBasket;

    private Context context;

    private Rect rectBasket = new Rect();
    private Rect rectBall = new Rect();
    private float mStartX;
    private float mStartY;
    private float mStartDistance;
    private float x;
    private float y;

    private int[] locationStart = new int[2];
    private int[] location = new int[2];

    public BaseketballView(Context context) {
        this(context, null);
    }

    public BaseketballView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseketballView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseketballView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        this.context = context.getApplicationContext();

        LayoutInflater.from(context).inflate(R.layout.view_basketball, this);
        ButterKnife.bind(this);

        ivBasketball.post(new Runnable() {
            @Override
            public void run() {
                mStartDistance = ivBasketball.getTop() - (ivBasket.getTop() + ivBasket.getHeight());
                ivBasketball.getLocationOnScreen(locationStart);
                x = locationStart[0];
                y = locationStart[1];
                ivBasketball.getGlobalVisibleRect(rectBall);
                ivBasket.getGlobalVisibleRect(rectBasket);
                Log.e("x", String.valueOf(x));
                Log.e("y", String.valueOf(y));
                Log.e("ivBasket", String.valueOf(rectBasket.left) + " / " + rectBasket.top + " / " + rectBasket.right + " / " + rectBasket.bottom);

                ivBasketball.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        int action = event.getAction();
                        float startX = event.getX();
                        float startY = event.getY();
                        Log.e("startX", String.valueOf(startX));
                        Log.e("startY", String.valueOf(startY));
                        Log.e("ivBasketball.getX()", String.valueOf(ivBasketball.getX()));
                        Log.e("ivBasketball.getY()", String.valueOf(ivBasketball.getY()));
                        switch (action) {
                            case MotionEvent.ACTION_DOWN:
                                mStartX = startX;
                                mStartY = startY;
                                break;
                            case MotionEvent.ACTION_MOVE:
                                moveBall(startX, startY);
                                break;
                            case MotionEvent.ACTION_UP:
                                doTriggerEvent();
                                break;
                        }

                        return true;
                    }
                });
            }
        });
    }

    private void moveBall(float startX, float startY) {
        float moveX = mStartX - startX;
        float moveY = mStartY - startY;

//        ivBasketball.setImageMatrix();
//        testTranslate(ivBasketball);
//        ivBasketball.setTranslationX(-moveX);
//        ivBasketball.setTranslationY(-moveY);
        int left = (int) (ivBasketball.getLeft() - moveX);
        int top = (int) (ivBasketball.getTop() - moveY);
        ivBasketball.layout(left, top, left + ivBasketball.getWidth(), top + ivBasketball.getHeight());
    }

    //平移
    private void testTranslate(MatrixImageView imageView) {
        Matrix matrix = new Matrix();
        int width = imageView.getBitmap().getWidth();
        int height = imageView.getBitmap().getHeight();
        matrix.postTranslate(width, height);
        imageView.setImageMatrix(matrix);
        showMatrixEveryValue(matrix);
    }

    //获取变换矩阵Matrix中的每个值
    private void showMatrixEveryValue(Matrix matrix){
        float matrixValues []=new float[9];
        matrix.getValues(matrixValues);
        for (int i = 0; i <3; i++) {
            String valueString="";
            for (int j = 0; j < 3; j++) {
                valueString=matrixValues[3*i+j]+"";
                System.out.println("第"+(i+1)+"行的第"+(j+1)+"列的值为"+valueString);
            }
        }
    }

    private void doTriggerEvent() {
        final int x = (int) ivBasketball.getX();
        final int y = (int) ivBasketball.getY();
        if (rectBasket.contains(x, y)) {
            //解锁
            EventBus.getDefault().post(new FinishEvent());
        } else {
//            TranslateAnimation animation = new TranslateAnimation(0, 0, 150, 0);
            TranslateAnimation animation = new TranslateAnimation(x, mStartX, y, mStartY);
            slideView(ivBasketball, x, this.x, y, this.y, 200, 0);
        }
    }

    public static void slideView(final View view, final float fx, final float tx, final float fy, final float ty, long durationMillis, long delayMillis) {
        TranslateAnimation animation = new TranslateAnimation(fx, tx, fy, ty);
        animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(durationMillis);
        animation.setStartOffset(delayMillis);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                int left = view.getLeft() + (int) (tx - fx);
                int top = view.getTop() + (int) (ty - fy);
                int width = view.getWidth();
                int height = view.getHeight();
                view.clearAnimation();
                view.layout(left, top, left + width, top + height);
            }
        });
        view.startAnimation(animation);
    }
}
