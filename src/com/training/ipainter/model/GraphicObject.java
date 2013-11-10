/**
 * 
 */
package com.training.ipainter.model;

import android.graphics.Paint;

/**
 * @author chenwei
 *
 */
public abstract class GraphicObject implements IDrawable {

    protected Paint mPaint;

    public GraphicObject() {
        mPaint = new Paint();
    }

    @Override
    public void resetPaint(Paint paint) {
        if(paint == null) {
            throw new IllegalArgumentException("paint can be be null.");
        }
        mPaint.set(paint);
    }

    /**
     * @param x
     * @param y
     * @return
     */
    public boolean containsPoint(int x, int y) {
        return false;
    }

    public void adjustPosition(int dx, int dy) {
    }

}
