/**
 * 
 */
package com.training.ipainter.core;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * @author chenwei
 *
 */
public class PainterThread extends Thread {

    private static final String TAG = "PainterThread";

    private Object mLocker = new Object();
    private SurfaceHolder mHolder;
    private boolean mExitMark = false;

    public PainterThread(SurfaceHolder holder) {
        mHolder = holder;
    }

    @Override
    public void run() {

        while (true) {
            try {
                Log.d(TAG, "waiting for signal.");
                synchronized (mLocker) {
                    mLocker.wait();
                }
                Log.d(TAG, "signal arrived.");
                if (getExitMark()) {
                    break;
                }

                HandleSignal();

            } catch (InterruptedException e) {
                e.printStackTrace();
                // set exit mark and notify to
                // let thread exit at next while loop
                setExitMark(true);
                synchronized (mLocker) {
                    mLocker.notify();
                }
            } finally {
            }
        }
        Log.d(TAG, "PainterThread exited.");
    }

    public void sendSignal() {
        synchronized (mLocker) {
            mLocker.notify();
        }
    }

    // TODO test code, need refactor
    public void HandleSignal() {

        Canvas canvas = null;

        canvas = mHolder.lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.BLUE);
            Paint p = new Paint();
            p.setAntiAlias(true);
            p.setColor(Color.rgb(0, 0, 0));
            p.setStrokeWidth(10);
            p.setStrokeCap(Cap.ROUND);
            canvas.drawCircle(100, 100, 20, p);

            mHolder.unlockCanvasAndPost(canvas);
        }
    }

    public synchronized boolean getExitMark() {
        return mExitMark;
    }

    public synchronized void setExitMark(boolean mark) {
        mExitMark = mark;
    }
}
