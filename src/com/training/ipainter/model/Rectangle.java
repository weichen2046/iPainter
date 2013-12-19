/**
 * 
 */
package com.training.ipainter.model;

import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * @author chenwei
 *
 */
public class Rectangle extends Shape {

    public Rectangle(Rect rect) {
        mBounds.set(rect);
    }

    public Rectangle(int left, int top, int right, int bottom) {
        mBounds.set(left, top, right, bottom);
    }

    @Override
    public void drawSelf(Canvas canvas) {
        // TODO Auto-generated method stub
        canvas.drawRect(mBounds, mPaint);
    }

}
