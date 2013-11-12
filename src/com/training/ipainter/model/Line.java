/**
 * 
 */
package com.training.ipainter.model;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

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
    public void drawSelf(Canvas canvas) {
        canvas.drawLine(mStart.x, mStart.y, mStop.x, mStop.y, mPaint);
    }

    @Override
    public boolean containsPoint(int x, int y) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getGraphicObjType() {
        return GraphicObject.GRAPHIC_LINE_TYPE;
    }

    @Override
    public boolean isIntersectWith(Rect rect) {
        // TODO Auto-generated method stub
        return false;
    }

}
