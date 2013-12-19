package com.training.ipainter.model;

import com.training.ipainter.utils.RectUtil;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

public class Ellipse extends Shape {

    // only for drawSelf(...)
    private RectF mBoundsF;

    public Ellipse(RectF src) {
        mBoundsF = new RectF();
        RectUtil.truncRectFToRect(src, mBounds);
    }

    @Override
    public void drawSelf(Canvas canvas) {
        mBoundsF.set(mBounds);
        canvas.drawOval(mBoundsF, mPaint);
    }

    @Override
    public boolean isIntersectWith(Rect rect) {
        // TODO current we use the bounds rect
        // but if we use the ellipse itself is a better implementation
        return Rect.intersects(mBounds, rect);
    }

}
