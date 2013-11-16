/**
 * 
 */
package com.training.ipainter.model;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * @author chenwei
 *
 */
public interface IDrawable {

    /**
     * 
     * @param canvas
     * @param paint
     */
    void drawSelf(Canvas canvas);

    void setPaint(Paint paint);

    void adjustPosition(int dx, int dy);

    boolean containsPoint(int x, int y);

    // TODO may be this will be change to isIntersectWith(IDrawable drawable).
    boolean isIntersectWith(Rect rect);

    void add(IDrawable drawable);

    Rect getBounds();
}
