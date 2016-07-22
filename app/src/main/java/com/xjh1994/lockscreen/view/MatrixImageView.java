package com.xjh1994.lockscreen.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by xjh1994 on 2016/7/22.
 */
public class MatrixImageView extends ImageView {

    private Matrix mMatrix;
    private Bitmap mBitmap;

    public MatrixImageView(Context context) {
        this(context, null);
    }

    public MatrixImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MatrixImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mMatrix = new Matrix();
        mBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        System.out.println("---> onDraw");
        //画原图
        canvas.drawBitmap(mBitmap, 0, 0, null);
        //画经过Matrix变化后的图
        canvas.drawBitmap(mBitmap, mMatrix, null);
        super.onDraw(canvas);
    }

    @Override
    public void setImageMatrix(Matrix matrix) {
        System.out.println("---> setImageMatrix");
        this.mMatrix.set(matrix);
        super.setImageMatrix(matrix);
    }

    public Bitmap getBitmap() {
        System.out.println("---> getBitmap");
        return mBitmap;
    }

}
