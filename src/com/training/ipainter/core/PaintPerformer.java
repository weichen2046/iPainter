/**
 * 
 */
package com.training.ipainter.core;

import android.util.Log;
import android.view.SurfaceHolder;

/**
 * @author chenwei
 *
 */
public class PaintPerformer {

    private static final String TAG = "PaintPerformer";

    private PainterThread mThread;
    private SurfaceHolder mHolder;

    private boolean mIsActive;

    private int mStartPointX;
    private int mStartPointY;
    private int mEndPointX;
    private int mEndPointY;
    private int mMovePointX;
    private int mMovePointY;

    public PaintPerformer(SurfaceHolder holder) {
        mHolder = holder;
        mThread = new PainterThread(mHolder);
    }

    public void startWork() {
        if (!isRunning()) {
            turnOn();
            mThread.start();
            return;
        }
        Log.d(TAG, "PaintPerformer is already running, doing nothing.");
    }

    public void stopWork() {
        if (!isRunning()) {
            Log.d(TAG, "PaintPerformer is not running now, doing nothing.");
            return;
        }

        turnOff();
        mThread.setExitMark(true);
        mThread.sendSignal();
        mThread = null;
    }

    public void turnOn() {
        mIsActive = true;
    }

    public void turnOff() {
        mIsActive = false;
    }

    public boolean isRunning() {
        return mIsActive;
    }

    public void doPaint() {
        mThread.sendSignal();
    }

    public void actionDown(int x, int y) {
        mStartPointX = x;
        mStartPointY = y;
        doPaint();
    }

    public void actionUp(int x, int y) {
        mEndPointX = x;
        mEndPointY = y;
        doPaint();
    }

    public void actionMove(int x, int y) {
        doPaint();
    }
}
