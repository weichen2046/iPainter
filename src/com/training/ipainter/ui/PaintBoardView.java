package com.training.ipainter.ui;

import java.util.LinkedList;
import java.util.Stack;

import com.training.ipainter.drawingtools.DrawingToolsManager;
import com.training.ipainter.model.IDrawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

public class PaintBoardView extends View {

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private float mSX;
    private float mSY;

    private DrawingToolsManager mToolsManager;
    private Stack<IDrawable> mDrawableObjs;

    public PaintBoardView(Context context) {
        super(context);

        mToolsManager = DrawingToolsManager.getInstance();
        mDrawableObjs = new Stack<IDrawable>();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mBitmap != null) {
            mBitmap.recycle();
        }
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        // repaint all graphic objects
        redrawAllGraphicObjects();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    private void touch_start(float x, float y) {
        mSX = x;
        mSY = y;
    }

    private void touch_move(float x, float y) {
        // TODO
    }

    private void touch_up(float x, float y) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            touch_start(x, y);
            break;
        case MotionEvent.ACTION_MOVE:
            touch_move(x, y);
            break;
        case MotionEvent.ACTION_UP:
            touch_up(x, y);
            break;
        }
        return true;
    }

    private void redrawAllGraphicObjects() {
        // clear board by repaint the background
        mCanvas.drawColor(mToolsManager.getBoardBackgroundColor());
        for (IDrawable drawable : mDrawableObjs) {
            drawable.drawSelf(mCanvas, mToolsManager.getPaint());
        }
    }

}
