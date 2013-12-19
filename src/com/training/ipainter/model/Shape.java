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

    @Override
    public boolean containsPoint(int x, int y) {
        return mBounds.contains(x, y);
    }

    @Override
    public int getGraphicObjType() {
        return GRAPHIC_SHAPE_TYPE;
    }

    @Override
    public void adjustPosition(int dx, int dy) {
        mBounds.offset(dx, dy);
    }

    @Override
    public boolean isIntersectWith(Rect rect) {
        return Rect.intersects(mBounds, rect);
    }

}
