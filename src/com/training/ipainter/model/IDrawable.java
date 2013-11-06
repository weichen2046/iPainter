/**
 * 
 */
package com.training.ipainter.model;

import android.graphics.Canvas;

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

    /**
     * 
     * @param x
     * @param y
     * @return
     */
    boolean containsPoint(float x, float y);

}
