package com.xjh1994.lockscreen.unlock;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by XJH on 16/3/14.
 */
public class HomeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("HomeActivity", "OnCreate");
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("HomeActivity", "OnFinish");
    }
}
