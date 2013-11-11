package com.training.ipainter.ui;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.training.ipainter.drawingtools.DrawingToolsManager;
import com.training.ipainter.drawingtools.DrawingToolsManager.OnConfigureChangeListener;
import com.training.ipainter.model.GraphicObject;
import com.training.ipainter.model.IDrawable;
import com.training.ipainter.model.Rectangle;

public class PaintBoardView extends View implements
        OnConfigureChangeListener {

    private static final String TAG = "PaintBoardView";

    private Bitmap mBitmap;
    private Bitmap mBitmap4Backup;
    private Canvas mCanvas;
    private Canvas mCanvas4Backup;
    private Paint mPaint;
    private Paint mDashPaint;
    private Rect mRectDirty;
    private GraphicObject mSelectedGraphic;
    private int mMode;
    private int mBrushType;
    private float mSX;
    private float mSY;
    private float mPX;
    private float mPY;

    private DrawingToolsManager mToolsManager;
    private List<IDrawable> mDrawingHistories;

    public PaintBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mToolsManager = DrawingToolsManager.getInstance();
        mDrawingHistories = new LinkedList<IDrawable>();

        mToolsManager.registerConfigureChangeEvent(this);
        
        mRectDirty = new Rect();

        // init dash paint for drawing when finger move
        initDashPaint();
    }

    private void initDashPaint() {
        mDashPaint = new Paint();
        mDashPaint.setAntiAlias(true);
        mDashPaint.setStyle(Style.STROKE);
        PathEffect effects = new DashPathEffect(new float[] {
                5, 5, 5, 5
        }, 1);
        mDashPaint.setPathEffect(effects);
        mDashPaint.setStrokeWidth(1);
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

    private void touchStart(float x, float y) {
        mSX = x;
        mSY = y;

        switch (mMode) {
            case DrawingToolsManager.PAINT_MODE:
                // backup current bitmap on paint board
                mCanvas4Backup.drawBitmap(mBitmap, 0, 0, null);
                break;
            case DrawingToolsManager.SELECT_MODE:
                // if is select mode, we need to check is any drawable object
                // under start point
                // if has one drawable object under this point we can move it
                // when finger move or we can start to select multiple drawable
                // objects.
                mSelectedGraphic = getFirstDrawableOnPoint((int) x, (int) y);
                mPX = x;
                mPY = y;
                break;
            default:
                Log.d(TAG, "Unknown mode in touchStart.");
                break;
        }
    }

    private void touchMove(float x, float y) {
        switch (mMode) {
            case DrawingToolsManager.PAINT_MODE:
                doPaintModeWhenMove(x, y);
                break;
            case DrawingToolsManager.SELECT_MODE:
                doSelectModeWhenMove(x, y);
                break;
            default:
                Log.d(TAG, "Unknown mode in touch_move.");
                break;
        }
    }

    private void touchUp(float x, float y) {
        // TODO
        // new IDrawable object and add to
        switch (mMode) {
            case DrawingToolsManager.PAINT_MODE:
                doPaintModeWhenUp(x, y);
                break;
            case DrawingToolsManager.SELECT_MODE:
                mSelectedGraphic = null;
                break;
            default:
                Log.d(TAG, "Unknown mode in touch_up.");
                break;
        }
        // TODO the next line is for test and need remove
        mPaint.setColor(mToolsManager.getRandomColor());
    }

    private void doPaintModeWhenMove(float x, float y) {
        // TODO can refactor to use region or rect for efficiency
        mCanvas.drawBitmap(mBitmap4Backup, 0, 0, null);
        switch (mBrushType) {
            case DrawingToolsManager.BRUSH_LINE:
                break;
            case DrawingToolsManager.BRUSH_RECT:
                // 1 we convert float to int here
                // because we only save these int values in our Rectangle
                // when finger up
                // 2 we intentionally shrink the rectangle by 1 pix here
                // to avoid the dash rectangle always showing when we fill
                // the same size of rectangle
                mCanvas.drawRect((int) mSX + 1, (int) mSY + 1, (int) x - 1, (int) y - 1,
                        mDashPaint);
                this.invalidate();
                break;
            case DrawingToolsManager.BRUSH_CIRCLE:
                break;
            default:
                Log.d(TAG, "Unknown brush type.");
                break;
        }
    }

    private void doSelectModeWhenMove(float x, float y) {
        if (mSelectedGraphic == null) {
            // start select multiple GraphicObject
        } else {
            Log.d(TAG, "doSelectMode for one.");
            // start move select GraphicObject
            float dx = x - mPX;
            float dy = y - mPY;
            mSelectedGraphic.adjustPosition((int) dx, (int) dy);
            redrawAllGraphicObjects();
            this.invalidate();
            mPX = x;
            mPY = y;
        }
    }

    private void doPaintModeWhenUp(float x, float y) {
        // TODO need refactoring for using Factory Pattern
        IDrawable drawable = null;
        switch (mBrushType) {
            case DrawingToolsManager.BRUSH_LINE:
                break;
            case DrawingToolsManager.BRUSH_RECT:
                mCanvas.drawRect((int) mSX, (int) mSY, (int) x, (int) y, mPaint);
                drawable = new Rectangle((int) mSX, (int) mSY, (int) x, (int) y);
                drawable.resetPaint(mPaint);
                break;
            case DrawingToolsManager.BRUSH_CIRCLE:
                break;
            default:
                Log.d(TAG, "Unknown brush type.");
                break;
        }
        if (drawable != null) {
            mDrawingHistories.add(drawable);
            Log.d(TAG, "new drawable object added, now size is: "
                    + mDrawingHistories.size());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
            break;
        case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
            break;
        case MotionEvent.ACTION_UP:
                touchUp(x, y);
            break;
        }
        return true;
    }

    private void redrawAllGraphicObjects() {
        // clear board by repaint the background
        mCanvas.drawColor(mToolsManager.getBoardBackgroundColor());
        for (IDrawable drawable : mDrawingHistories) {
            drawable.drawSelf(mCanvas);
        }
    }

    private GraphicObject getFirstDrawableOnPoint(int x, int y) {
        ListIterator<IDrawable> iter =
                mDrawingHistories.listIterator(mDrawingHistories.size());
        GraphicObject graphic = null;
        while(iter.hasPrevious()) {
            graphic = (GraphicObject) iter.previous();
            if (graphic.containsPoint(x, y)) {
                break;
            }
        }
        return graphic;
    }

    @Override
    public int getInterestingChangeSet() {
        return DrawingToolsManager.PAINT_CHANGE_FLAG
                | DrawingToolsManager.MODE_CHANGE_FLAG
                | DrawingToolsManager.BRUSH_TYPE_CHANGE_FLAG;
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
            mMode = mToolsManager.getMode();
            Log.d(TAG, "onModeChanged called, current mode: " + mMode);
        }
        if ((changedFlags & DrawingToolsManager.BRUSH_TYPE_CHANGE_FLAG)
                == DrawingToolsManager.BRUSH_TYPE_CHANGE_FLAG) {
            mBrushType = mToolsManager.getBrushType();
            Log.d(TAG, "onBrushTypeChanged called, current brush type: "
                    + mBrushType);
        }
        return 0;
    }

}
