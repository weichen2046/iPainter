/**
 * 
 */
package com.training.ipainter.model;

import android.graphics.Rect;

import com.training.ipainter.utils.RectCoordinateCorrector;

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

    /**
     * return a rect that left always not large than right and top always not
     * large than bottom.
     * 
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @return
     */
    public static Rect getNormalRect(int sX, int sY, int eX, int eY) {
        RectCoordinateCorrector corrector = new RectCoordinateCorrector();
        corrector.rectifyCoordinate(sX, sY, eX, eY);

        return new Rect(corrector.mLeft, corrector.mTop,
                corrector.mRight, corrector.mBottom);
    }

}
