/**
 * 
 */
package com.training.ipainter.model;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

import com.training.ipainter.utils.RectUtil;

/**
 * @author chenwei
 *
 */
public class Line extends GraphicObject {

    private static final int VIRTUAL_BORDER_WIDTH = 10;
    private Point mStart;
    private Point mEnd;

    public Line(int sX, int sY, int eX, int eY) {
        mStart = new Point(sX, sY);
        mEnd = new Point(eX, eY);
        RectUtil.rectifyCoordinate(mBounds, sX, sY, eX, eY);
        initVirualBounds();
    }

    @Override
    public void drawSelf(Canvas canvas) {
        canvas.drawLine(mStart.x, mStart.y, mEnd.x, mEnd.y, mPaint);
        // following lines only for debug
        //Paint.Style style = mPaint.getStyle();
        //mPaint.setStyle(Style.STROKE);
        //canvas.drawRect(mBounds, mPaint);
        //mPaint.setStyle(style);
    }

    @Override
    public boolean containsPoint(int x, int y) {
        // TODO current we use virtual bounds to judge if a point be contains
        // by a line, a better implementation would be judge if a point locate
        // on a line and take line width into consideration.
        return mBounds.contains(x, y);
    }

    @Override
    public void setBounds(Rect rect) {
        super.setBounds(rect);
        // additional reset the start and end points
        mStart.set(rect.left, rect.top);
        mEnd.set(rect.right, rect.bottom);
    }

    @Override
    public boolean isIntersectWith(Rect rect) {
        return Rect.intersects(rect, mBounds);
    }

    public void adjustPosition(int dx, int dy) {
        mBounds.offset(dx, dy);
        mStart.offset(dx, dy);
        mEnd.offset(dx, dy);
    }

    private void initVirualBounds() {
        if (mStart.x == mEnd.x) {
            RectUtil.inflate(mBounds, VIRTUAL_BORDER_WIDTH, 0);
        }
        if (mStart.y == mEnd.y) {
            RectUtil.inflate(mBounds, 0, VIRTUAL_BORDER_WIDTH);
        }
    }

    @Override
    public int getGraphicObjType() {
        return GRAPHIC_LINE_TYPE;
    }

}
