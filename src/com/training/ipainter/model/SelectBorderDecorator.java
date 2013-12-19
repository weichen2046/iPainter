package com.training.ipainter.model;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class SelectBorderDecorator extends DrawableDecorator {

    private static final int BORDER_RECT_WIDTH = 5;

    public SelectBorderDecorator(IDrawable drawable) {
        super(drawable);
    }

    @Override
    public void drawSelf(Canvas canvas) {
        super.drawSelf(canvas);
        GraphicObject graphic = (GraphicObject) mDrawable;
        if (graphic != null) {
            Rect bounds = graphic.getBounds();
            // draw select border
            switch (graphic.getGraphicObjType()) {
                case GraphicObject.GRAPHIC_SHAPE_TYPE:
                    drawSelectedMarksAroundTheBounds(canvas, bounds,
                            graphic.mPaint);
                    break;
                case GraphicObject.GRAPHIC_LINE_TYPE:
                    // current we use the same logic with rectangle to draw
                    // selected mark for line, a better implementation would be
                    // draw marks alone with the line's angle but not the rect
                    // bounds of the line, like other painter software does.
                    drawSelectedMarksAroundTheBounds(canvas, bounds,
                            graphic.mPaint);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void adjustPosition(int dx, int dy) {
        mDrawable.adjustPosition(dx, dy);
    }

    @Override
    public boolean containsPoint(int x, int y) {
        return mDrawable.containsPoint(x, y);
    }

    @Override
    public boolean isIntersectWith(Rect rect) {
        return mDrawable.isIntersectWith(rect);
    }

    @Override
    public void setBounds(Rect rect) {
        mDrawable.setBounds(rect);
    }

    private void drawSelectedMarksAroundTheBounds(Canvas canvas, Rect bounds,
           Paint paint) {
        // left top corner
        canvas.drawRect(bounds.left - BORDER_RECT_WIDTH,
                bounds.top - BORDER_RECT_WIDTH,
                bounds.left, bounds.top, paint);
        // right top corner
        canvas.drawRect(bounds.right,
                bounds.top - BORDER_RECT_WIDTH,
                bounds.right + BORDER_RECT_WIDTH,
                bounds.top, paint);
        // left bottom corner
        canvas.drawRect(bounds.left - BORDER_RECT_WIDTH,
                bounds.bottom,
                bounds.left,
                bounds.bottom + BORDER_RECT_WIDTH, paint);
        // right bottom corner
        canvas.drawRect(bounds.right,
                bounds.bottom,
                bounds.right + BORDER_RECT_WIDTH,
                bounds.bottom + BORDER_RECT_WIDTH, paint);
    }
}
