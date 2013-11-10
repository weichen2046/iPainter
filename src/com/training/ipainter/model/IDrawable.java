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

    void resetPaint(Paint paint);
}
