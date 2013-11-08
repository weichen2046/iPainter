/**
 * 
 */
package com.training.ipainter.model;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

/**
 * @author chenwei
 *
 */
public class Line extends GraphicObject {

    private Point mStart;
    private Point mStop;

    /* (non-Javadoc)
     * @see com.training.ipainter.model.IDrawable#drawSelf(android.graphics.Canvas, android.graphics.Paint)
     */
    @Override
    public void drawSelf(Canvas canvas, Paint paint) {
        canvas.drawLine(mStart.x, mStart.y, mStop.x, mStop.y, paint);
    }

    @Override
    public boolean containsPoint(float x, float y) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void initPaint(Paint paint) {
        // TODO Auto-generated method stub

    }

}
