package com.training.ipainter.model;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public abstract class DrawableDecorator implements IDrawable {

    protected IDrawable mDrawable;

    public DrawableDecorator(IDrawable drawable) {
        if(drawable == null) {
            throw new IllegalArgumentException("drawable must not be null.");
        }
        mDrawable = drawable;
    }

    @Override
    public void drawSelf(Canvas canvas) {
        mDrawable.drawSelf(canvas);
    }

    @Override
    public void setPaint(Paint paint) {
        mDrawable.setPaint(paint);
    }

    @Override
    public Rect getBounds() {
        return mDrawable.getBounds();
    }

    @Override
    public void setBounds(Rect rect) {
        mDrawable.setBounds(rect);
    }

    public IDrawable getDrawable() {
        return mDrawable;
    }

    public void add(IDrawable drawable) {
        throw new UnsupportedOperationException();
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
