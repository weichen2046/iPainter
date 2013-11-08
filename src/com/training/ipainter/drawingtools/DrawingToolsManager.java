/**
 * 
 */
package com.training.ipainter.drawingtools;

import android.graphics.Color;
import android.graphics.Paint;

/**
 *
 */
public class DrawingToolsManager {

    private int mBoardBgColor;
    private Paint mPaint;

    private DrawingToolsManager() {
        mBoardBgColor = Color.WHITE;
        mPaint = new Paint();
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

    public int getBoardBackgroundColor() {
        return mBoardBgColor;
    }

    public void setBoardBackgroundColor(int color) {
        int oldColor = mBoardBgColor;
        mBoardBgColor = color;
    }

    public Paint getPaint() {
        return mPaint;
    }

    // public void registerBoardBackgroundColorChangeEvent()

    // public class OnConfigChangeListener {
    //
    // }

}
