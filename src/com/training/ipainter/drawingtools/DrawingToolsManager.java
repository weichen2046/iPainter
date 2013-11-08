/**
 * 
 */
package com.training.ipainter.drawingtools;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Color;
import android.graphics.Paint;

/**
 *
 */
public class DrawingToolsManager {

    private int mBoardBgColor;
    private Paint mPaint;
    private int mMode;
    private int mBrushType;

    private List<OnConfigureChangeListener> mConfigureChangedListeners;

    public static final int SELECT_MODE = 0;
    public static final int PAINT_MODE = 1;

    public static final int BRUSH_LINE = 0;
    public static final int BRUSH_RECT = 1;
    public static final int BRUSH_CIRCLE = 2;

    public static final int MODE_CHANGE_FLAG = 1;
    public static final int BRUSH_TYPE_CHANGE_FLAG = MODE_CHANGE_FLAG << 1;
    public static final int PAINT_CHANGE_FLAG = BRUSH_TYPE_CHANGE_FLAG << 1;
    public static final int ALL_CHANGE_FLAG = 0xFFFFFFFF;

    private DrawingToolsManager() {
        resetToDefault();
    }

    /**
     * Singleton pattern. Initialization-on-demand holder idiom. The original
     * implementation from Bill Pugh.
     * 
     */
    private static class LazyHolder {
        private static final DrawingToolsManager INSTANCE = new DrawingToolsManager();
    }

    public static DrawingToolsManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    private void resetToDefault() {
        mBoardBgColor = Color.WHITE;
        mPaint = new Paint();
        mMode = PAINT_MODE;
        mBrushType = BRUSH_RECT;
    }

    public int getBoardBackgroundColor() {
        return mBoardBgColor;
    }

    public void setBoardBackgroundColor(int color) {
        //int oldColor = mBoardBgColor;
        mBoardBgColor = color;
        // TODO notify background color changed event
    }

    public Paint getPaint() {
        return mPaint;
    }

    // TODO need refactor
    public void setPaint(Paint paint) {
        mPaint = null;
        mPaint = paint;
        // triggerPaintChangedEvent();
        triggerConfigureChangedEvent(PAINT_CHANGE_FLAG);
    }

    public void registerConfigureChangeEvent(
            OnConfigureChangeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException(
                    "Parameter listener can not be null.");
        }
        if (mConfigureChangedListeners == null) {
            mConfigureChangedListeners =
                    new LinkedList<OnConfigureChangeListener>();
        }
        listener.onConfigureChanged(ALL_CHANGE_FLAG);
        mConfigureChangedListeners.add(listener);

    }

    public int getMode() {
        return mMode;
    }

    public void setMode(int mode) {
        mMode = mode;
        // triggerModeChangedEvent();
        triggerConfigureChangedEvent(MODE_CHANGE_FLAG);
    }

    public int getBrushType() {
        return mBrushType;
    }

    public void setBrushType(int brushType) {
        mBrushType = brushType;
        // triggerBrushTypeChangedEvent();
        triggerConfigureChangedEvent(BRUSH_TYPE_CHANGE_FLAG);
    }

    public interface OnConfigureChangeListener {
        int getInteresingChangeSet();
        int onConfigureChanged(int changedFlags);
    }

    private void triggerConfigureChangedEvent(int changedFlags) {
        if (mConfigureChangedListeners != null) {
            for (OnConfigureChangeListener listener : mConfigureChangedListeners) {
                if ((listener.getInteresingChangeSet() & changedFlags) != 0) {
                    listener.onConfigureChanged(changedFlags);
                }
            }
        }
    }

}
