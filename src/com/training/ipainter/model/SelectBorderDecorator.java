package com.training.ipainter.model;

import android.graphics.Canvas;
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
            // draw select border
            switch (graphic.getGraphicObjType()) {
                case GraphicObject.GRAPHIC_SHAPE_TYPE:
                    Rect bounds = ((Shape) graphic).mBounds;
                    // left top corner
                    canvas.drawRect(bounds.left - BORDER_RECT_WIDTH,
                            bounds.top - BORDER_RECT_WIDTH,
                            bounds.left, bounds.top, graphic.mPaint);
                    // right top corner
                    canvas.drawRect(bounds.right,
                            bounds.top - BORDER_RECT_WIDTH,
                            bounds.right + BORDER_RECT_WIDTH,
                            bounds.top, graphic.mPaint);
                    // left bottom corner
                    canvas.drawRect(bounds.left - BORDER_RECT_WIDTH,
                            bounds.bottom,
                            bounds.left,
                            bounds.bottom + BORDER_RECT_WIDTH, graphic.mPaint);
                    // right bottom corner
                    canvas.drawRect(bounds.right,
                            bounds.bottom,
                            bounds.right + BORDER_RECT_WIDTH,
                            bounds.bottom + BORDER_RECT_WIDTH, graphic.mPaint);
                    break;
                case GraphicObject.GRAPHIC_LINE_TYPE:
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

}
