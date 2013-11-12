/**
 * 
 */
package com.training.ipainter.model;

import android.graphics.Canvas;
import android.graphics.Paint;

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
}
