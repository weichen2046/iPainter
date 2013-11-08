/**
 * 
 */
package com.training.ipainter.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * @author chenwei
 *
 */
public class Rectangle extends Shape {

    @Override
    public void drawSelf(Canvas canvas, Paint paint) {
        // TODO Auto-generated method stub
        initPaint(paint);
        canvas.drawRect(mBounds, paint);
    }

    @Override
    public boolean containsPoint(float x, float y) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void initPaint(Paint paint) {
        paint.reset();
        // color
        paint.setColor(Color.GREEN);
        // width
        // TODO other attribute associate to Paint
    }

}
