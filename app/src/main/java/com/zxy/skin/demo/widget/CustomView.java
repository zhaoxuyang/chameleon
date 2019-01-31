package com.zxy.skin.demo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import com.zxy.skin.demo.R;

public class CustomView extends View {

    private int mLineColor = Color.BLACK;

    private Paint mPaint;

    private int mRadius = 0;

    private PointF mCenter = new PointF();

    public CustomView(Context context) {
        this(context, null);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomView);
        final int N = typedArray.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.CustomView_lineColor:
                    mLineColor = typedArray.getColor(attr, Color.BLACK);
                    break;
            }
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);//设置空心
        mPaint.setStrokeWidth(5);
        mPaint.setColor(mLineColor);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = 0;
        int height = 0;
        if(widthMode==MeasureSpec.EXACTLY && heightMode==MeasureSpec.EXACTLY){
            width = MeasureSpec.getSize(widthMeasureSpec);
            height = MeasureSpec.getSize(heightMeasureSpec);
            mRadius = Math.min(width,height)/2;
            mCenter.set(width/2,height/2);
        }
        setMeasuredDimension(width,height);
    }

    public void setLineColor(int color){
        mLineColor = color;
        mPaint.setColor(mLineColor);
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mCenter.x, mCenter.y, mRadius, mPaint);
    }
}
