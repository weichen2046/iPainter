package com.training.ipainter.model;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.Log;

public class DragableDecorator extends DrawableDecorator {

    private static final String TAG = "DragableDecorator";
    private int mInitX;
    private int mInitY;
    private int mCurrentX;
    private int mCurrentY;
    private Rect mDragHintBounds;
    private Rect mCurrentFitBounds;
    private Rect mOriginBounds;
    private Rect mFitBounds;
    private static final int DRAG_SQUARE_WIDTH = 200;

    public DragableDecorator(IDrawable drawable, int initX, int initY) {
        super(drawable);

        mInitX = initX;
        mInitY = initY;
        mCurrentX = mInitX;
        mCurrentY = mInitY;
        mFitBounds = new Rect();
        mCurrentFitBounds = new Rect();
        mDragHintBounds = new Rect();

        initDragHintBounds();
        storeOriginalData();

        shakeToFitBoundsForDraging();
    }

    @Override
    public void drawSelf(Canvas canvas) {
        super.drawSelf(canvas);
        // draw a square around the scaled drawable
        GraphicObject graphic = (GraphicObject) mDrawable;
        if (graphic != null) {
            Paint.Style style = graphic.mPaint.getStyle();
            graphic.mPaint.setStyle(Style.STROKE);
            canvas.drawRect(mDragHintBounds, graphic.mPaint);
            graphic.mPaint.setStyle(style);
        }
    }

    @Override
    public void adjustPosition(int dx, int dy) {
        super.adjustPosition(dx, dy);
        mCurrentX += dx;
        mCurrentY += dy;
        mCurrentFitBounds.offset(dx, dy);
        mDragHintBounds.offset(dx, dy);
    }

    @Override
    public boolean containsPoint(int x, int y) {
        return false;
    }

    @Override
    public boolean isIntersectWith(Rect rect) {
        return false;
    }

    @Override
    public void setBounds(Rect rect) {
        // can't set bound to a DragableDecorator
    }

    private void shakeToFitBoundsForDraging() {

        int width = mOriginBounds.width();
        int height = mOriginBounds.height();

        Log.d(TAG, String.format("origin width: %d, height: %d, mInitX: %d, mInitY: %d",
                width, height, mInitX, mInitY));

        float scaleRatio = 1.0f;

        float maxSideLength = (width > height) ? width : height;
        scaleRatio = DRAG_SQUARE_WIDTH / maxSideLength;

        width = (int)(width * scaleRatio);
        height = (int)(height * scaleRatio);

        Log.d(TAG, String.format("after change, width: %d, height: %d, scale ratio: %f",
                width, height, scaleRatio));

        int halfWidth = (int)width/2;
        int halfHeight = (int)height/2;

        mFitBounds.set(mInitX - halfWidth,
                mInitY - halfHeight,
                // use width - halfWidth won't lost any length
                mInitX + (width - halfWidth),
                mInitY + (height - halfHeight));
        Log.d(TAG, "mFitBounds:" + mFitBounds.toString());
        mCurrentFitBounds.set(mFitBounds);
        mDrawable.setBounds(mFitBounds);
    }

    private void initDragHintBounds() {
        int halfWidth = DRAG_SQUARE_WIDTH / 2;
        mDragHintBounds.set(mInitX - halfWidth,
                mInitY - halfWidth,
                mInitX + (DRAG_SQUARE_WIDTH - halfWidth),
                mInitY + (DRAG_SQUARE_WIDTH - halfWidth));
    }

    private void storeOriginalData() {
        mOriginBounds = mDrawable.getBounds();
    }

    private void restoreOriginalData() {
        mDrawable.setBounds(mOriginBounds);
    }

}
