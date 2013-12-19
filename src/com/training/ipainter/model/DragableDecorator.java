package com.training.ipainter.model;

import android.graphics.Rect;

public class DragableDecorator extends DrawableDecorator {

    private Rect mOriginBounds;

    public DragableDecorator(IDrawable drawable) {
        super(drawable);
    }

    @Override
    public void adjustPosition(int dx, int dy) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean containsPoint(int x, int y) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isIntersectWith(Rect rect) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setBounds(Rect rect) {
        // TODO Auto-generated method stub

    }

    private void shakeToFitBoundsForDraging() {
        Rect fitBounds = new Rect();
        int width = mOriginBounds.width();
        int height = mOriginBounds.height();
        if (mOriginBounds.width() > mOriginBounds.height()) {

        } else {

        }
        mDrawable.setBounds(fitBounds);
    }

    private void storeOriginalData() {
        mOriginBounds = mDrawable.getBounds();
    }

    private void restoreOriginalData() {
        mDrawable.setBounds(mOriginBounds);
    }

}
