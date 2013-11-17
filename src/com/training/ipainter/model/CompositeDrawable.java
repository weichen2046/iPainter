package com.training.ipainter.model;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class CompositeDrawable extends Shape {

    private List<IDrawable> mDrawables = new LinkedList<IDrawable>();

    @Override
    public void drawSelf(Canvas canvas) {
        for (IDrawable drawable : mDrawables) {
            drawable.drawSelf(canvas);
        }
    }

    @Override
    public void setPaint(Paint paint) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void adjustPosition(int dx, int dy) {
        mBounds.offset(dx, dy);
        for (IDrawable drawable : mDrawables) {
            drawable.adjustPosition(dx, dy);
        }
    }

    @Override
    public boolean isIntersectWith(Rect rect) {
        for (IDrawable drawable : mDrawables) {
            if (drawable.isIntersectWith(rect)) {
                return true;
            }
        }
        return false;
    }

    /**
     * the z-order of drawables according to the order they be added.
     */
    @Override
    public void add(IDrawable drawable) {
        if(drawable == null) {
            throw new NullPointerException("drawable must not be null.");
        }
        if (mDrawables.indexOf(drawable) != -1) {
            throw new DrawableAlreadyExistException("can not add the same drawable.");
        }
        mDrawables.add(drawable);
        // update mBounds
        mBounds.union(drawable.getBounds());
    }

    public List<IDrawable> getCompositedDrawables() {
        if (mDrawables.size() == 0) {
            return null;
        }
        return mDrawables;
    }

}
