package com.training.ipainter.ui;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.training.ipainter.drawingtools.DrawingToolsManager;
import com.training.ipainter.drawingtools.DrawingToolsManager.OnConfigureChangeListener;
import com.training.ipainter.model.IDrawable;

public class PaintBoardView extends View implements
        OnConfigureChangeListener {

    private static final String TAG = "PaintBoardView";

    private Bitmap mBitmap;
    private Bitmap mBitmap4Backup;
    private Canvas mCanvas;
    private Canvas mCanvas4Backup;
    private Paint mPaint;
    private int mMode;
    private int mBrushType;
    private float mSX;
    private float mSY;

    private DrawingToolsManager mToolsManager;
    private List<IDrawable> mDrawingHistories;

    public PaintBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mToolsManager = DrawingToolsManager.getInstance();
        mDrawingHistories = new LinkedList<IDrawable>();

        mToolsManager.registerConfigureChangeEvent(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged called.");
        if (mBitmap != null) {
            mBitmap.recycle();
        }
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        // only need create once
        if (mBitmap4Backup == null) {
            mBitmap4Backup = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas4Backup = new Canvas(mBitmap4Backup);
        }

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

        // backup current bitmap on paint board
        mCanvas4Backup.drawBitmap(mBitmap, 0, 0, null);
    }

    private void touch_move(float x, float y) {
        switch (mMode) {
            case DrawingToolsManager.PAINT_MODE:
                doPaintModeWhenMove(x, y);
                break;
            case DrawingToolsManager.SELECT_MODE:
                break;
            default:
                Log.d(TAG, "Unknown mode in touch_move.");
                break;
        }
    }

    private void touch_up(float x, float y) {
        // TODO
        // new IDrawable object and add to
        switch (mMode) {
            case DrawingToolsManager.PAINT_MODE:
                doPaintModeWhenUp(x, y);
                break;
            case DrawingToolsManager.SELECT_MODE:
                break;
            default:
                Log.d(TAG, "Unknown mode in touch_up.");
                break;
        }
    }

    private void doPaintModeWhenMove(float x, float y) {
        // TODO can refactor to use region or rect for efficiency
        mCanvas.drawBitmap(mBitmap4Backup, 0, 0, null);
        switch (mBrushType) {
            case DrawingToolsManager.BRUSH_LINE:
                break;
            case DrawingToolsManager.BRUSH_RECT:
                mCanvas.drawRect(mSX, mSY, x, y, mPaint);
                this.invalidate();
                break;
            case DrawingToolsManager.BRUSH_CIRCLE:
                break;
            default:
                Log.d(TAG, "Unknown brush type.");
                break;
        }
    }

    private void doPaintModeWhenUp(float x, float y) {
        // TODO need refactoring for using Factory Pattern
        IDrawable drawable = null;
        switch (mBrushType) {
            case DrawingToolsManager.BRUSH_LINE:
                break;
            case DrawingToolsManager.BRUSH_RECT:
                break;
            case DrawingToolsManager.BRUSH_CIRCLE:
                break;
            default:
                Log.d(TAG, "Unknown brush type.");
                break;
        }
        if (drawable != null) {
            mDrawingHistories.add(drawable);
        }
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
        for (IDrawable drawable : mDrawingHistories) {
            drawable.drawSelf(mCanvas, mToolsManager.getPaint());
        }
    }

    @Override
    public int getInteresingChangeSet() {
        return DrawingToolsManager.PAINT_CHANGE_FLAG;
    }

    @Override
    public int onConfigureChanged(int changedFlags) {
        if ((changedFlags & DrawingToolsManager.PAINT_CHANGE_FLAG)
                == DrawingToolsManager.PAINT_CHANGE_FLAG) {
            Log.d(TAG, "onPaintChanged called.");
            mPaint = mToolsManager.getPaint();
        }
        if ((changedFlags & DrawingToolsManager.MODE_CHANGE_FLAG)
                == DrawingToolsManager.MODE_CHANGE_FLAG) {
            Log.d(TAG, "onModeChanged called.");
            mMode = mToolsManager.getMode();
        }
        if ((changedFlags & DrawingToolsManager.BRUSH_TYPE_CHANGE_FLAG)
                == DrawingToolsManager.BRUSH_TYPE_CHANGE_FLAG) {
            Log.d(TAG, "onBrushTypeChanged called.");
            mBrushType = mToolsManager.getBrushType();
        }
        return 0;
    }

}
