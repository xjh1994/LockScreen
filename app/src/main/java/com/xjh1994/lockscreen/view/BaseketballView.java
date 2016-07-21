package com.xjh1994.lockscreen.view;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xjh1994.lockscreen.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xjh1994 on 2016/7/21.
 */
public class BaseketballView extends RelativeLayout {

    @Bind(R.id.iv_basketball)
    ImageView ivBasketball;
    @Bind(R.id.iv_basket)
    ImageView ivBasket;

    private Context context;

    private float mStartX;
    private float mStartY;
    private float mStartDistance;
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

        mStartDistance = ivBasketball.getTop() - (ivBasket.getTop() + ivBasket.getHeight());
        ivBasketball.getLocationOnScreen(locationStart);
        y = ivBasketball.getY();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float startY = event.getY();
        Log.d("startY", String.valueOf(startY));
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartY = startY;
                break;
            case MotionEvent.ACTION_MOVE:
                moveBall(startY);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                doTriggerEvent(startY);
                break;
        }

        return true;
    }

    private void moveBall(float startY) {
        float move = mStartY - startY;
        if (move < 0) {
            move = 0;
        }
        ivBasketball.setTranslationY(-move);
    }

    private void doTriggerEvent(float startY) {
        float move = mStartY - startY;
        if (move > mStartDistance) {
            //解锁
            Toast.makeText(context, "unlock", Toast.LENGTH_SHORT).show();
        } else {
            ivBasketball.getLocationOnScreen(location);
            int s = locationStart[1] - location[1];
            ObjectAnimator animator = ObjectAnimator.ofFloat(ivBasketball, "y", y);
            animator.setDuration(300).start();
        }
    }
}
