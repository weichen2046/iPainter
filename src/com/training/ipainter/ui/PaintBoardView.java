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
import com.training.ipainter.model.DrawableDecorator;
import com.training.ipainter.model.IDrawable;
import com.training.ipainter.model.Rectangle;
import com.training.ipainter.model.SelectBorderDecorator;

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
    private IDrawable mSelectedDrawable;
    private DrawableDecorator mSelectBorderDecorator;
    private int mMode;
    private int mBrushType;
    private float mSX;
    private float mSY;
    private float mPX;
    private float mPY;
    private boolean mIsSelectMultiple;

    private DrawingToolsManager mToolsManager;
    private List<IDrawable> mDrawingHistories;

    public PaintBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mToolsManager = DrawingToolsManager.getInstance();
        mDrawingHistories = new LinkedList<IDrawable>();
        mRectDirty = new Rect();
        mMode = DrawingToolsManager.UNKNOWN_MODE;

        // init dash paint for drawing when finger move
        initDashPaint();

        // make this register be the last code line in this construct
        // because we need prepare all fields first
        mToolsManager.registerConfigureChangeEvent(this);
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
                doSelectModeStart(x, y);
                break;
            default:
                Log.d(TAG, "Unknown mode in touchStart.");
                break;
        }

        // TODO the next line is for test and need remove
        mPaint.setColor(mToolsManager.getRandomColor());
    }

    private void touchMove(float x, float y) {
        switch (mMode) {
            case DrawingToolsManager.PAINT_MODE:
                doPaintModeMove(x, y);
                break;
            case DrawingToolsManager.SELECT_MODE:
                doSelectModeMove(x, y);
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
                doSelectModeUp(x, y);
                break;
            default:
                Log.d(TAG, "Unknown mode in touch_up.");
                break;
        }
    }

    private void doPaintModeMove(float x, float y) {
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

    private void doSelectModeMove(float x, float y) {
        // start select multiple GraphicObject, draw dash rect
        if (mIsSelectMultiple) {
            // the next line because we backup current paint board when
            // we detected multi-select on action down. so we restore it
            // for no need to redraw all drawable object in mDrawingHistories
            // list like call redrawAllGraphicObjects()
            mCanvas.drawBitmap(mBitmap4Backup, 0, 0, null);
            mCanvas.drawRect((int) mSX + 1, (int) mSY + 1, (int) x - 1, (int) y - 1,
                    mDashPaint);
            this.invalidate();
        } else {
            Log.d(TAG, "doSelectMode for one.");
            // start move select GraphicObject
            float dx = x - mPX;
            float dy = y - mPY;
            mSelectedDrawable.adjustPosition((int) dx, (int) dy);
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
                drawable.setPaint(mPaint);
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
        // if we have prepared mCanvas we can draw it now
        // because mCanvas only be created after onSizeChanged be called
        // but this method will be call after
        if (mCanvas != null) {
            // clear board by repaint the background
            mCanvas.drawColor(mToolsManager.getBoardBackgroundColor());
            for (IDrawable drawable : mDrawingHistories) {
                drawable.drawSelf(mCanvas);
            }
        }
    }

    private IDrawable getFirstDrawableOnPoint(int x, int y) {
        ListIterator<IDrawable> iter =
                mDrawingHistories.listIterator(mDrawingHistories.size());
        IDrawable drawable = null;
        IDrawable retVal = null;
        while(iter.hasPrevious()) {
            drawable = iter.previous();
            if (drawable.containsPoint(x, y)) {
                retVal = drawable;
                break;
            }
        }
        return retVal;
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
            doModeChange(mToolsManager.getMode());
        }
        if ((changedFlags & DrawingToolsManager.BRUSH_TYPE_CHANGE_FLAG)
                == DrawingToolsManager.BRUSH_TYPE_CHANGE_FLAG) {
            mBrushType = mToolsManager.getBrushType();
            Log.d(TAG, "onBrushTypeChanged called, current brush type: "
                    + mBrushType);
        }
        return 0;
    }

    private void doModeChange(int newMode) {
        int prevMode = mMode;
        mMode = newMode;

        if (prevMode == DrawingToolsManager.SELECT_MODE
                && newMode == DrawingToolsManager.PAINT_MODE) {
            if (mIsSelectMultiple) {
                mIsSelectMultiple = false;
                // no need to remove dash rect for the dash is disappear when
                // finger up
                // only need to remove all selected indicators
                clearAllSelectedIndicators();
                redrawAllGraphicObjects();
                this.invalidate();
            } else {
                // if we previous has one selected drawable
                // remove the selected decorator
                removeSelectedBorderDecorator();
                redrawAllGraphicObjects();
                this.invalidate();
            }
        }
        Log.d(TAG, "onModeChanged called, current mode: " + mMode);
    }

    private void removeSelectedBorderDecorator() {
        if (mSelectBorderDecorator != null) {
            // has previous select, delete it from
            // mDrawingHistories
            int replaceIndex =
                    mDrawingHistories.indexOf(mSelectBorderDecorator);
            if (-1 != replaceIndex) {
                mDrawingHistories.add(replaceIndex,
                        mSelectBorderDecorator.getDrawable());
                mDrawingHistories.remove(mSelectBorderDecorator);
                mSelectBorderDecorator = null;
            }
        }
    }

    private void doSelectModeStart(float x, float y) {
        // if is select mode, we need to check is any drawable object
        // under start point
        // if has one drawable object under this point we can move it
        // when finger move or we can start to select multiple drawable
        // objects.

        // TODO if we need a context menu for long touch event to
        // composite already selected multi drawable object, there may be a
        // bug if clear all selected indicator.
        clearAllSelectedIndicators();
        // redraw all drawable object to mBitmap to avoid some code
        // backup current status that may be show's selected indicators
        redrawAllGraphicObjects();

        mSelectedDrawable = getFirstDrawableOnPoint((int) x, (int) y);
        if (mSelectedDrawable != null) {
            if (mSelectedDrawable instanceof DrawableDecorator) {
                // previous select, doing nothing
            } else {
                removeSelectedBorderDecorator();
                // TODO may be new a SelectBorderDecorator not here
                // new a SelectBorderDecorator
                mSelectBorderDecorator =
                        new SelectBorderDecorator(mSelectedDrawable);
                // replace it to mDrawingHistories
                int replaceIndex =
                        mDrawingHistories.indexOf(mSelectedDrawable);
                if (-1 != replaceIndex) {
                    mDrawingHistories.add(replaceIndex, mSelectBorderDecorator);
                    mDrawingHistories.remove(mSelectedDrawable);
                }
            }
            mIsSelectMultiple = false;
        } else {
            removeSelectedBorderDecorator();
            // if previous multiple select not finish, we don't need to backup
            // the current panit board or the dash rect will also be backup
            if (!mIsSelectMultiple) {
                // backup current bitmap on paint board
                mCanvas4Backup.drawBitmap(mBitmap, 0, 0, null);
                mIsSelectMultiple = true;
            }
        }
        mPX = x;
        mPY = y;
    }

    private void doSelectModeUp(float x, float y) {
        // mSelectedDrawable may be a GraphicObject, may be a
        // SelectedBorderDecorator
        // TODO we don't need the next line
        mSelectedDrawable = null;

        if (mIsSelectMultiple) {
            // erase the dash rect
            // make all drawable object wrap with SelectedBorderDecorator
            Rect dashRect = new Rect((int) mSX, (int) mSY, (int) x, (int) y);

            IDrawable drawable = null;
            for (int i = 0; i < mDrawingHistories.size(); i++) {
                drawable = mDrawingHistories.get(i);
                if (drawable.isIntersectWith(dashRect)) {
                    mDrawingHistories.add(i,
                            new SelectBorderDecorator(drawable));
                    mDrawingHistories.remove(i + 1);
                }
            }
            // redraw all drawable object in mDrawingHistories
            redrawAllGraphicObjects();
            // TODO is there need call this.invalidate()?
            // I have test it, seems no need to call this.invalidate(), but I
            // am not sure is there any implicit issues.
        }
    }

    // TODO this function is unused now
    private boolean wrapSelectIndicator(IDrawable drawable) {
        if (drawable == null) {
            // throw Exception
        }
        int index = mDrawingHistories.indexOf(drawable);
        if (index != -1) {
            if (!(drawable instanceof DrawableDecorator)) {
                mDrawingHistories.add(index,
                        new SelectBorderDecorator(drawable));
                mDrawingHistories.remove(drawable);
            } else {
                // drawable is already a DrawableDecorator
                // may be a special case here, caution!
            }
        }
        return false;
    }

    private void clearAllSelectedIndicators() {
        IDrawable drawable = null;
        DrawableDecorator decorator = null;
        for (int i = 0; i < mDrawingHistories.size(); i++) {
            drawable = mDrawingHistories.get(i);
            if (drawable instanceof DrawableDecorator) {
                decorator = (DrawableDecorator) drawable;
                mDrawingHistories.add(i, decorator.getDrawable());
                mDrawingHistories.remove(i + 1);
            }
        }
    }

}
