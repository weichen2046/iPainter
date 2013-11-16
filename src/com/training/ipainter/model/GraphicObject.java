/**
 * 
 */
package com.training.ipainter.model;

import android.graphics.Paint;
import android.graphics.Rect;

/**
 * @author chenwei
 *
 */
public abstract class GraphicObject implements IDrawable {

    protected Paint mPaint;

    public static final int GRAPHIC_LINE_TYPE = 0;
    public static final int GRAPHIC_SHAPE_TYPE = 1;
    public static final int GRAPHIC_COMPOSITE_TYPE = 2;

    /**
     * the smallest rectangle that can hold this shape.
     */
    protected Rect mBounds;

    public GraphicObject() {
        mPaint = new Paint();
        mBounds = new Rect();
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

    public void add(IDrawable drawable) {
        throw new UnsupportedOperationException();
    }

    public Rect getBounds() {
        return mBounds;
    }
}
