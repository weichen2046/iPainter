/**
 * 
 */
package com.training.ipainter.model;

import android.graphics.Rect;

/**
 * @author chenwei
 *
 */
public abstract class Shape extends GraphicObject {

    /**
     * the smallest rectangle that can hold this shape.
     */
    protected Rect mBounds;

    public Shape() {
        mBounds = new Rect();
    }

    @Override
    public boolean containsPoint(int x, int y) {
        return mBounds.contains(x, y);
    }

}
