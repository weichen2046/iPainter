/**
 * 
 */
package com.training.ipainter.core;

import android.graphics.Bitmap;
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
    private Bitmap mPrevBitmap;

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

    public void setPaintingBoard(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        if (mPrevBitmap == null) {
            // first set
            mThread.setBitmap(bitmap);
        } else {
            Bitmap reuseBitmap = mPrevBitmap;
            mThread.setBitmap(bitmap);
            reuseBitmap.recycle();
            // TODO need refactor for bitmap recycle
            // when new bitmap created, pass this signal to PaintPerformer
            // and recycle prev bitmap object if it not null.
        }
        mPrevBitmap = bitmap;
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
        Rectangle rect = new Rectangle(mStartX, mStartY, mPrevX, mPrevY);
        sig.setDrawable(rect);
        Paint paint = rect.getPaint();
        // erase origin
        // paint.setXfermode(new PorterDuffXfermode(Mode.XOR));
        // doPaint(sig);
        // paint new
        sig = Signal.obtain();
        rect = new Rectangle(mStartX, mStartY, x, y);
        sig.setDrawable(rect);
        // paint = rect.getPaint();
        // paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
        doPaint(sig);
        mPrevX = x;
        mPrevY = y;
    }
}
