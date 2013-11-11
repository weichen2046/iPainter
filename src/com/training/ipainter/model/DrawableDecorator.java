package com.training.ipainter.model;

import android.graphics.Canvas;
import android.graphics.Paint;

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
    public void resetPaint(Paint paint) {
        mDrawable.resetPaint(paint);
    }

    public IDrawable getDrawable() {
        return mDrawable;
    }

}