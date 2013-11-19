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
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.training.ipainter.drawingtools.DrawingToolsManager;
import com.training.ipainter.drawingtools.DrawingToolsManager.INotifyReceiver;
import com.training.ipainter.model.CompositeDrawable;
import com.training.ipainter.model.DrawableDecorator;
import com.training.ipainter.model.IDrawable;
import com.training.ipainter.model.MementoManager;
import com.training.ipainter.model.Rectangle;
import com.training.ipainter.model.SelectBorderDecorator;
import com.training.ipainter.model.Shape;
import com.training.ipainter.utils.RectCoordinateCorrector;

public class PaintBoardView extends View implements INotifyReceiver {

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
    private List<IDrawable> mDrawables4Composing;

    private MementoManager mMementoManager;

    // handler for bring tools panel up, defined in PainterActivity
    private Handler mHander4Parent;
    private DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
    private boolean mStartToBringToolsPanel = false;
    // if finger moved distance larger than
    // MIN_MOVE_DISTANCE_FOR_TRIGGER_TOOLS_PANEL and tools panel be marked to
    // show up, then we start to bring tools panel show up
    private static final int MIN_MOVE_DISTANCE_FOR_TRIGGER_TOOLS_PANEL = 10;
    // if the distance between finger start point and right side of the screen
    // we mark tools panel to be show up to true, 8 is a expeirential value
    private static final int MIN_WIDTH_FOR_MARK_TOOLS_PANEL_TRIGGER_START = 8;

    // use to rectify rect coordinate to keep left always not large than right
    // and top always not large than bottom
    private RectCoordinateCorrector mRectCoordinateCorrector;

    public PaintBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mToolsManager = DrawingToolsManager.getInstance();
        mDrawingHistories = new LinkedList<IDrawable>();
        mDrawables4Composing = new LinkedList<IDrawable>();
        mRectDirty = new Rect();
        mMode = DrawingToolsManager.UNKNOWN_MODE;
        mRectCoordinateCorrector = new RectCoordinateCorrector();
        mMementoManager = MementoManager.getInstance();
        mMementoManager.setDrawingHistories(mDrawingHistories);

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
            onModeChanging(mToolsManager.getMode());
        }
        if ((changedFlags & DrawingToolsManager.BRUSH_TYPE_CHANGE_FLAG)
                == DrawingToolsManager.BRUSH_TYPE_CHANGE_FLAG) {
            mBrushType = mToolsManager.getBrushType();
            Log.d(TAG, "onBrushTypeChanged called, current brush type: "
                    + mBrushType);
        }
        return 0;
    }

    @Override
    public void onActions(int actions) {
        if ((actions & DrawingToolsManager.COMPOSITE_ACTION)
                == DrawingToolsManager.COMPOSITE_ACTION) {
            Log.d(TAG, "Composite action received.");
            doComposite();
        } else if ((actions & DrawingToolsManager.DECOMPOSITE_ACTION)
                == DrawingToolsManager.DECOMPOSITE_ACTION) {
            Log.d(TAG, "Decomposite action received.");
            doDecomposite();
        } else if ((actions & DrawingToolsManager.UNDO_ACTION)
                == DrawingToolsManager.UNDO_ACTION) {
            Log.d(TAG, "Undo action received.");
            undo();
        } else if ((actions & DrawingToolsManager.REDO_ACTION)
                == DrawingToolsManager.REDO_ACTION) {
            Log.d(TAG, "Redo action received.");
            redo();
        }
    }

    // TODO not a good design
    // this function is called by PainterActivity and store the handler for
    // sending msg to bring tools panel up
    public void setHandler(Handler handler) {
        mHander4Parent = handler;
    }

    private void touchStart(float x, float y) {
        mSX = x;
        mSY = y;
        // TODO here we only take screen portrait situation into consideration
        if (x > mDisplayMetrics.widthPixels - MIN_WIDTH_FOR_MARK_TOOLS_PANEL_TRIGGER_START
                && mStartToBringToolsPanel == false) {
            mStartToBringToolsPanel = true;
        } else {
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
        }
    }

    private void touchMove(float x, float y) {
        if (mStartToBringToolsPanel
                && (mSX - x) > MIN_MOVE_DISTANCE_FOR_TRIGGER_TOOLS_PANEL) {
            // 2 bring tools panel front of parent
            Message msg = mHander4Parent.obtainMessage(PainterActivity.BORDER_TO_BACK);
            mHander4Parent.sendMessage(msg);
        } else {
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
    }

    private void touchUp(float x, float y) {
        // TODO
        // new IDrawable object and add to
        if (mStartToBringToolsPanel) {
            mStartToBringToolsPanel = false;
        } else {
            switch (mMode) {
                case DrawingToolsManager.PAINT_MODE:
                    doPaintModeUp(x, y);
                    break;
                case DrawingToolsManager.SELECT_MODE:
                    doSelectModeUp(x, y);
                    break;
                default:
                    Log.d(TAG, "Unknown mode in touch_up.");
                    break;
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

        // not allow composable or decomposable
        mToolsManager
                .setComposableStatus(DrawingToolsManager.COMPOSABLE_DECOMPOSABLE_BOTH_DISABLE);

        mSelectedDrawable = getFirstDrawableOnPoint((int) x, (int) y);
        if (mSelectedDrawable != null) {
            mMementoManager.setPrevRect(mSelectedDrawable.getBounds());
            if (mSelectedDrawable instanceof DrawableDecorator) {
                // TODO this route will never reach, must delete
            } else {
                removeCurrentSelectedIndicators();
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
            removeCurrentSelectedIndicators();
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
                mRectCoordinateCorrector.rectifyCoordinate((int) mSX, (int) mSY, (int) x, (int) y);
                mCanvas.drawRect(mRectCoordinateCorrector.mLeft + 1,
                        mRectCoordinateCorrector.mTop + 1,
                        mRectCoordinateCorrector.mRight - 1,
                        mRectCoordinateCorrector.mBottom - 1,
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
            Log.d(TAG, String.format("dx: %d, dy: %d", (int) dx, (int) dy));
            mSelectedDrawable.adjustPosition((int) dx, (int) dy);
            redrawAllGraphicObjects();
            this.invalidate();
            mPX = x;
            mPY = y;
        }
    }

    private void doPaintModeUp(float x, float y) {
        // TODO need refactoring for using Factory Pattern
        IDrawable drawable = null;
        switch (mBrushType) {
        case DrawingToolsManager.BRUSH_LINE:
            break;
        case DrawingToolsManager.BRUSH_RECT:
            mCanvas.drawRect((int) mSX, (int) mSY, (int) x, (int) y, mPaint);
            mRectCoordinateCorrector.rectifyCoordinate((int) mSX, (int) mSY,
                    (int) x, (int) y);
            drawable = new Rectangle(mRectCoordinateCorrector.mLeft,
                    mRectCoordinateCorrector.mTop,
                    mRectCoordinateCorrector.mRight,
                    mRectCoordinateCorrector.mBottom);
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
            // TODO create undoable memento
            mMementoManager.addCreationMemento(drawable, mDrawingHistories.size() - 1);
        }
    }

    private void doSelectModeUp(float x, float y) {
        // mSelectedDrawable may be a GraphicObject, may be a
        // SelectedBorderDecorator

        if (mIsSelectMultiple) {
            // erase the dash rect
            // make all drawable object wrap with SelectedBorderDecorator
            Rect dashRect = Shape.getNormalRect((int) mSX, (int) mSY, (int) x, (int) y);

            IDrawable drawable = null;
            mDrawables4Composing.clear();
            SelectBorderDecorator decorator = null;
            for (int i = 0; i < mDrawingHistories.size(); i++) {
                drawable = mDrawingHistories.get(i);
                if (drawable.isIntersectWith(dashRect)) {
                    decorator = new SelectBorderDecorator(drawable);
                    mDrawingHistories.add(i, decorator);
                    mDrawingHistories.remove(i + 1); // remove origin object
                    mDrawables4Composing.add(decorator);
                }
            }
            // redraw all drawable object in mDrawingHistories
            redrawAllGraphicObjects();
            // TODO is there need call this.invalidate()?
            // I have test it, seems no need to call this.invalidate(), but I
            // am not sure is there any implicit issues.

            // if only select one or select no drawable, we think this must lead
            // to single select status. we update the value of mIsSelectMultiple
            // and we can use the newest value such as update menu etc.
            if (mDrawables4Composing.size() < 2) {
                mIsSelectMultiple = false;
                mToolsManager
                        .setComposableStatus(DrawingToolsManager.COMPOSABLE_DECOMPOSABLE_BOTH_DISABLE);
                // if the only one drawable object is a composite drawable
                // we need to mark allow decomposable action.
                if (mDrawables4Composing.size() == 1
                        && (mDrawables4Composing.get(0) instanceof CompositeDrawable)) {
                            mToolsManager
                                .setComposableStatus(DrawingToolsManager.DECOMPOSABLE_ENABLE);
                }
            } else {
                mToolsManager
                        .setComposableStatus(DrawingToolsManager.COMPOSABLE_ENABLE);
            }
        } else {
            mMementoManager.setCurrentRect(mSelectedDrawable.getBounds());
            if (!(mSelectedDrawable == null)
                    && (mSelectedDrawable instanceof CompositeDrawable)) {
                mToolsManager
                        .setComposableStatus(DrawingToolsManager.DECOMPOSABLE_ENABLE);
            }
            // TODO create undoable memento
            if (mMementoManager.isPositionChanged()) {
                mMementoManager.addPositionChangedMemento(mSelectedDrawable);
            }
        }
    }

    private void doComposite() {
        if (mDrawables4Composing.size() > 1) {
            CompositeDrawable comp = new CompositeDrawable();
            IDrawable drawable = null;
            int index = -1;
            List<Integer> indexes = new LinkedList<Integer>();
            int removedCount = 0;
            // z-order is the same order with index in this list
            for (int i = 0; i < mDrawables4Composing.size(); i++) {
                drawable = mDrawables4Composing.get(i);
                // the last object in mDrawables4Composing
                index = mDrawingHistories.indexOf(drawable);
                indexes.add(index + removedCount);
                if ((i + 1) == mDrawables4Composing.size()) {
                    // the composite drawable object's z-order in
                    // mDrawingHistories will equals to the last drawable object
                    // in mDrawables4Composing's z-order in mDrawingHistories
                    mSelectedDrawable = comp;
                    mDrawingHistories.add(index, new SelectBorderDecorator(comp));
                }
                mDrawingHistories.remove(drawable);
                removedCount++;
                if (drawable instanceof SelectBorderDecorator) {
                    comp.add(((SelectBorderDecorator) drawable).getDrawable());
                } else {
                    // error, only a SelectBorderDecorator
                    Log.e(TAG,
                            "Error, not a SelectBorderDecorator is not a selected drawable");
                }
            }
            // TODO create undoable memento
            mMementoManager.addCompositeMemento(comp, indexes, index);
            mDrawables4Composing.clear();
            // refresh current painter board
            redrawAllGraphicObjects();
            this.invalidate();
            // allow decomposable
            mToolsManager
                    .setComposableStatus(DrawingToolsManager.DECOMPOSABLE_ENABLE);
        }
    }

    private void doDecomposite() {
        if (!(mSelectedDrawable == null)
                && (mSelectedDrawable instanceof CompositeDrawable)) {
            CompositeDrawable composites = (CompositeDrawable) mSelectedDrawable;
            // only a selected CompositeDrawable can be decomposite
            List<IDrawable> drawables = composites.getCompositedDrawables();
            if (composites != null && drawables != null) {
                // int insertIndex =
                // mDrawingHistories.indexOf(mSelectedDrawable);
                int insertIndex = indexOfDrawableIgnoreSelectBorderDecorator(mSelectedDrawable);
                // TODO create undoable memento
                mMementoManager.addDecompositeMemento(composites, insertIndex);
                mDrawingHistories.remove(insertIndex);
                for (IDrawable drawable : drawables) {
                    // do insert
                    mDrawingHistories.add(insertIndex++, drawable);
                }
                // make last drawable selected
                mSelectedDrawable = mDrawingHistories.remove(insertIndex - 1);
                mDrawingHistories.add(new SelectBorderDecorator(mSelectedDrawable));
                redrawAllGraphicObjects();
                this.invalidate();
            }
        }
    }

    private void undo() {
        mMementoManager.undo();
        redrawAllGraphicObjects();
        this.invalidate();
    }

    private void redo() {
        mMementoManager.redo();
        redrawAllGraphicObjects();
        this.invalidate();
    }

    private void onModeChanging(int newMode) {
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
                removeCurrentSelectedIndicators();
                redrawAllGraphicObjects();
                this.invalidate();
            }
        }
        Log.d(TAG, "onModeChanged called, current mode: " + mMode);
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

    private void removeCurrentSelectedIndicators() {
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

    private int indexOfDrawableIgnoreSelectBorderDecorator(IDrawable drawable) {
        int index = 0;
        for (IDrawable drawObj : mDrawingHistories) {
            if(drawObj instanceof SelectBorderDecorator) {
                if(((SelectBorderDecorator)drawObj).getDrawable() == drawable) {
                    return index;
                }
            } else if (drawObj == drawable) {
                return index;
            }
            index++;
        }
        return -1;
    }

}
