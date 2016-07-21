package com.xjh1994.lockscreen.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.xjh1994.lockscreen.R;

import butterknife.ButterKnife;

/**
 * Created by xjh1994 on 2016/7/21.
 */
public class BaseketballView extends RelativeLayout {

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
        LayoutInflater.from(context).inflate(R.layout.view_basketball, this);
        ButterKnife.bind(this);
    }

}
