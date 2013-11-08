/**
 * 
 */
package com.training.ipainter.core;

import java.util.LinkedList;
import java.util.Queue;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 *
 */
public class PainterThread extends Thread {

    private static final String TAG = "PainterThread";

    private Object mLocker = new Object();
    private SurfaceHolder mHolder;
    private boolean mExitMark = false;

    private Queue<Signal> mQueue;

    public PainterThread(SurfaceHolder holder) {
        mHolder = holder;
        mQueue = new LinkedList<Signal>();
    }

    @Override
    public void run() {
        Signal signal = null;
        while (true) {
            try {
                Log.d(TAG, "waiting for signal.");
                synchronized (mLocker) {
                    if (mQueue.size() == 0) {
                        mLocker.wait();
                        Log.d(TAG, "signal arrived.");
                    }
                    Log.d(TAG, "mQueue.size() = " + mQueue.size());
                    signal = mQueue.poll();
                }
                if (getExitMark()) {
                    break;
                }

                HandleSignal(signal);

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
        sendSignal(null);
    }

    public void sendSignal(Signal signal) {
        synchronized (mLocker) {
            if (signal != null) {
                mQueue.offer(signal);
            }
            mLocker.notify();
        }
    }

    // TODO test code, need refactor
    public void HandleSignal(Signal signal) {

        Canvas canvas = null;

        canvas = mHolder.lockCanvas();
        if (canvas != null) {
            if (signal != null && signal.mGraphicObj != null) {
                // signal.mGraphicObj.drawSelf(canvas);
            } else {
                Log.e(TAG, "signal or signal.mGraphicObj is null.");
            }
            mHolder.unlockCanvasAndPost(canvas);
        }

        // after done with signal, recycle it
        // signal.recycle();
        signal = null;
    }

    public synchronized boolean getExitMark() {
        return mExitMark;
    }

    public synchronized void setExitMark(boolean mark) {
        mExitMark = mark;
    }
}
