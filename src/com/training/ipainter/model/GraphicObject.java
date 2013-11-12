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

    public static final int GRAPHIC_LINE_TYPE = 0;
    public static final int GRAPHIC_SHAPE_TYPE = 1;

    public GraphicObject() {
        mPaint = new Paint();
    }

    @Override
    public void setPaint(Paint paint) {
        if(paint == null) {
            throw new IllegalArgumentException("paint can be be null.");
        }
        mPaint.set(paint);
    }

    public void adjustPosition(int dx, int dy) {
    }

    public abstract int getGraphicObjType();
}
