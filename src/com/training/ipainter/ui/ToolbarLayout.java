
package com.training.ipainter.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.training.ipainter.R;
import com.training.ipainter.drawingtools.DrawingToolsManager;
import com.training.ipainter.model.MementoManager;

public class ToolbarLayout extends LinearLayout implements View.OnClickListener,
        ColorPickerDialog.OnColorChangedListener {

    private static final String TAG = "ToolbarLayout";

    // tools panel is scrolled out or not, true for scrolled out.
    private boolean mIsOutside;

    private Scroller mScroller;
    private DrawingToolsManager mToolsManager;
    private MementoManager mMementoManager;
    private ColorPickerDialog mColorPicker;
    private Paint mPaint;
    private Handler mHander4Parent;

    private Button mModeChanger;
    private Button mComposition;
    private Button mUndo;
    private Button mRedo;

    private static final int TOOLS_MENU_BRUSH_LINE = 0;
    private static final int TOOLS_MENU_BRUSH_RECT = 1;
    private static final int TOOLS_MENU_BRUSH_CIRCLE = 2;
    private static final int TOOLS_MENU_MODE_CHANGE = 3;
    private static final int TOOLS_MENU_COLOR_PICKER = 4;
    private static final int TOOLS_MENU_COMPOSITION = 5;
    private static final int TOOLS_MENU_UNDO = 6;
    private static final int TOOLS_MENU_REDO = 7;

    public static final int TOOLS_PANEL_SHOW = 1;
    public static final int TOOLS_PANEL_HIDE = 2;

    private static final int MIN_LENGTH = 200;

    public interface OnScrollSideChangedListener {
        public void onScrollSideChanged(View v, boolean leftSide);
    }

    public void setHandler(Handler handler) {
        mHander4Parent = handler;
    }

    public ToolbarLayout(Context context) {
        super(context);
        initView(context);
    }

    public ToolbarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mMementoManager = MementoManager.getInstance();
        mToolsManager = DrawingToolsManager.getInstance();
        mPaint = mToolsManager.getPaint();

        mScroller = new Scroller(context,
                AnimationUtils.loadInterpolator(context,
                android.R.anim.overshoot_interpolator));

        // TODO change the title parameter to use resources
        mColorPicker = new ColorPickerDialog(context, Color.YELLOW,
                "color", this);
    }

    @Override
    public void colorChanged(int color) {
        mPaint.setColor(color);
        mToolsManager.setPaint(mPaint);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            postInvalidate();
        }
    }

    @Override
    protected void onFinishInflate() {

        Button BrushLine = (Button) findViewById(R.id.brushLine);
        BrushLine.setOnClickListener(this);
        BrushLine.setTag(TOOLS_MENU_BRUSH_LINE);

        Button BrushRect = (Button) findViewById(R.id.brushRect);
        BrushRect.setOnClickListener(this);
        BrushRect.setTag(TOOLS_MENU_BRUSH_RECT);

        Button BrushCircle = (Button) findViewById(R.id.brushCircle);
        BrushCircle.setOnClickListener(this);
        BrushCircle.setTag(TOOLS_MENU_BRUSH_CIRCLE);

        mModeChanger = (Button) findViewById(R.id.mode_changer);
        mModeChanger.setOnClickListener(this);
        mModeChanger.setTag(TOOLS_MENU_MODE_CHANGE);

        Button colorPicker = (Button) findViewById(R.id.colorPicker);
        colorPicker.setOnClickListener(this);
        colorPicker.setTag(TOOLS_MENU_COLOR_PICKER);

        mComposition = (Button) findViewById(R.id.composite);
        mComposition.setOnClickListener(this);
        mComposition.setTag(TOOLS_MENU_COMPOSITION);

        mUndo = (Button) findViewById(R.id.undo);
        mUndo.setOnClickListener(this);
        mUndo.setTag(TOOLS_MENU_UNDO);

        mRedo = (Button) findViewById(R.id.redo);
        mRedo.setOnClickListener(this);
        mRedo.setTag(TOOLS_MENU_REDO);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        super.onLayout(changed, left, top, right, bottom);
        int move = getWidth();

        for (int i = 0; i < getChildCount(); i++)
        {
            View child = getChildAt(i);
            child.layout(child.getLeft() + move, child.getTop(), child.getRight() + move,
                    child.getBottom());
        }
    }

    @Override
    public void onClick(View v) {

        int tag = (Integer) v.getTag();
        switch (tag) {
            case TOOLS_MENU_BRUSH_LINE:
                mToolsManager.setBrushType(DrawingToolsManager.BRUSH_LINE);
                break;
            case TOOLS_MENU_BRUSH_RECT:
                mToolsManager.setBrushType(DrawingToolsManager.BRUSH_RECT);
                break;
            case TOOLS_MENU_BRUSH_CIRCLE:
                mToolsManager.setBrushType(DrawingToolsManager.BRUSH_CIRCLE);
                break;
            case TOOLS_MENU_MODE_CHANGE:
                int currentMode = mToolsManager.getMode();
                int modeToBeSet = currentMode;
                if ( currentMode == DrawingToolsManager.SELECT_MODE) {
                    modeToBeSet = DrawingToolsManager.PAINT_MODE;
                } else if (currentMode == DrawingToolsManager.PAINT_MODE) {
                    modeToBeSet = DrawingToolsManager.SELECT_MODE;
                }
                mToolsManager.setMode(modeToBeSet);
                refreshModeChangeMenu();
                break;
            case TOOLS_MENU_COLOR_PICKER:
                mColorPicker.show();
                break;
            case TOOLS_MENU_COMPOSITION:
                if (mToolsManager.getComposableStatus()
                        == DrawingToolsManager.COMPOSABLE_ENABLE) {
                    mToolsManager.sendActions(DrawingToolsManager.COMPOSITE_ACTION);
                } else {
                    mToolsManager.sendActions(DrawingToolsManager.DECOMPOSITE_ACTION);
                }
                break;
            case TOOLS_MENU_UNDO:
                mToolsManager.sendActions(DrawingToolsManager.UNDO_ACTION);
                break;
            case TOOLS_MENU_REDO:
                mToolsManager.sendActions(DrawingToolsManager.REDO_ACTION);
                break;
            default:
                return;
        }

        scrollToScreen(TOOLS_PANEL_HIDE);
        Message msg = mHander4Parent.obtainMessage(PainterActivity.TOOLS_TO_BACK);
        mHander4Parent.sendMessage(msg);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                break;
            default:
                break;
        }
        return true;
    }

    public void scrollToScreen(int whichScreen) {

        int delta = 0;
        if (whichScreen == TOOLS_PANEL_SHOW) {
            delta = MIN_LENGTH - getScrollX();
            mIsOutside = true;
            // every time show tools panel, we refresh menus
            refeshMenus();
        } else if (whichScreen == TOOLS_PANEL_HIDE) {
            delta = -getScrollX();
            mIsOutside = false;
        } else {
            return;
        }

        mScroller.startScroll(getScrollX(), 0, delta, 0, 500);
        invalidate();
    }

    private void touchStart(float x, float y) {
        if (mIsOutside) {
            scrollToScreen(TOOLS_PANEL_HIDE);
            Message msg = mHander4Parent.obtainMessage(PainterActivity.TOOLS_TO_BACK);
            mHander4Parent.sendMessage(msg);
        }
    }

    private void refeshMenus() {
        // TOOLS_MENU_MODE_CHANGE
        refreshModeChangeMenu();

        // TOOLS_MENU_COMOSITION
        refreshCompositionMenu();

        // TOOLS_MENU_UNDO
        // TOOLS_MENU_REDO
        refreshUndoRedoMenu();
    }

    private void refreshModeChangeMenu() {
        int currentMode = mToolsManager.getMode();
        int titleResId = R.string.mode_select;
        if ( currentMode == DrawingToolsManager.SELECT_MODE) {
            titleResId = R.string.paint_mode;
        } else if (currentMode == DrawingToolsManager.PAINT_MODE) {
            titleResId = R.string.select_mode;
        }
        mModeChanger.setText(titleResId);
    }

    private void refreshCompositionMenu() {
        int status = mToolsManager.getComposableStatus();
        int titleResId;

        mComposition.setEnabled(true);
        if (status == DrawingToolsManager.COMPOSABLE_ENABLE) {
            titleResId = R.string.composite;
        } else if (status == DrawingToolsManager.DECOMPOSABLE_ENABLE) {
            titleResId = R.string.decomposite;
        } else {
            titleResId = R.string.composable_not_available;
            mComposition.setEnabled(false);
        }
        mComposition.setText(titleResId);
    }

    private void refreshUndoRedoMenu() {
        mUndo.setEnabled(mMementoManager.canUndo());
        mRedo.setEnabled(mMementoManager.canRedo());
    }
}
