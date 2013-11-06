/**
 * 
 */
package com.training.ipainter.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;

/**
 * @author chenwei
 *
 */
public class Rectangle extends Shape {

    private Paint mPaint;

    public Rectangle(int left, int top, int right, int bottom) {

        mBounds = new Rect(left, top, right, bottom);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.rgb(255, 0, 0));
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Style.FILL_AND_STROKE);
    }

    @Override
    public void drawSelf(Canvas canvas) {
        // TODO Auto-generated method stub
        canvas.drawRect(mBounds, mPaint);
    }

    @Override
    public boolean containsPoint(float x, float y) {
        // TODO Auto-generated method stub
        return false;
    }

    public Paint getPaint() {
        return mPaint;
    }

}
