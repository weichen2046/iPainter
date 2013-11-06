/**
 * 
 */
package com.training.ipainter.core;

import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.Log;
import android.view.SurfaceHolder;

import com.training.ipainter.model.Rectangle;

/**
 * @author chenwei
 *
 */
public class PaintPerformer {

    private static final String TAG = "PaintPerformer";

    private PainterThread mThread;
    private SurfaceHolder mHolder;

    private boolean mIsActive;

    private int mStartX;
    private int mStartY;
    private int mPrevX;
    private int mPrevY;

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

    private void doPaint(Signal signal) {
        mThread.sendSignal(signal);
    }

    public void actionDown(int x, int y) {
        // Signal sig = Signal.obtain();
        // doPaint(sig);
        mStartX = x;
        mStartY = y;
        mPrevX = x;
        mPrevY = y;
    }

    public void actionUp(int x, int y) {
        // Signal sig = Signal.obtain();
        // doPaint(sig);
        mStartX = 0;
        mStartY = 0;
    }

    public void actionMove(int x, int y) {
        Signal sig = Signal.obtain();
        Rectangle rect = new Rectangle(mStartX, mStartY, x, y);
        sig.setDrawable(rect);
        Paint paint = rect.getPaint();
        // erase origin
        paint.setXfermode(new PorterDuffXfermode(Mode.XOR));
        doPaint(sig);
        // paint new
        sig = Signal.obtain();
        rect = new Rectangle(mStartX, mStartY, x, y);
        sig.setDrawable(rect);
        paint = rect.getPaint();
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
        doPaint(sig);
        mPrevX = x;
        mPrevY = y;
    }
}
