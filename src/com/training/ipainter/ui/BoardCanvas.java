/**
 * 
 */
package com.training.ipainter.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.training.ipainter.core.PaintPerformer;

/**
 * @author chenwei
 *
 */
public class BoardCanvas extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "BoardCanvas";
    private SurfaceHolder mHolder;
    private PaintPerformer mPerformer;

    public BoardCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);

        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "" + event.getAction());
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPerformer.actionDown(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                mPerformer.actionMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mPerformer.actionUp(x, y);
                break;
        }
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated called.");
        getPerformer().startWork();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged called.");

        mPerformer.setPaintingBoard(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed called.");
        getPerformer().stopWork();
        mPerformer = null;
    }

    // TODO may be PaintPerformer can use Singleton Pattern
    private PaintPerformer getPerformer() {
        if (mPerformer == null) {
            mPerformer = new PaintPerformer(mHolder);
        }
        return mPerformer;
    }

}
