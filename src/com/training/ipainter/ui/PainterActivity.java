/**
 * 
 */
package com.training.ipainter.ui;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

import com.training.ipainter.R;

/**
 *
 */
public class PainterActivity extends Activity {

    private static final String TAG = "PainterActivity";
    private ToolbarLayout mToolbarPanel;
    private PaintBoardView mBoarderView;
    protected static final int BORDER_TO_BACK = 1;
    protected static final int TOOLS_TO_BACK = 2;
    private boolean mIsToolsPanelFront;

    private MyHandler mHander;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        mToolbarPanel = (ToolbarLayout)findViewById(R.id.my_scrollLayout);
        mBoarderView = (PaintBoardView)findViewById(R.id.paint_border);
        mHander = new MyHandler(this);
        mToolbarPanel.setHandler(mHander);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            int msgId;
            if (mIsToolsPanelFront) {
                msgId = PainterActivity.TOOLS_TO_BACK;
            } else {
                msgId = PainterActivity.BORDER_TO_BACK;
            }
            Message msg = mHander.obtainMessage(msgId);
            mHander.sendMessage(msg);
        }
        return super.onKeyDown(keyCode, event);
    }

    // Handler must be static or it will lead memory leak, see the reference
    // below
    // http://stackoverflow.com/questions/11407943/this-handler-class-should-be-static-or-leaks-might-occur-incominghandler
    // https://groups.google.com/forum/#!msg/android-developers/1aPZXZG6kWk/lIYDavGYn5UJ
    static class MyHandler extends Handler {

        private final WeakReference<PainterActivity> mActivity;

        MyHandler(PainterActivity activity) {
            mActivity = new WeakReference<PainterActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            PainterActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case BORDER_TO_BACK:
                        activity.mToolbarPanel.bringToFront();
                        activity.mToolbarPanel.scrollToScreen(
                                ToolbarLayout.TOOLS_PANEL_SHOW);
                        activity.mIsToolsPanelFront = true;
                        break;
                    case TOOLS_TO_BACK:
                        activity.mBoarderView.bringToFront();
                        activity.mToolbarPanel.scrollToScreen(
                                ToolbarLayout.TOOLS_PANEL_HIDE);
                        activity.mIsToolsPanelFront = false;
                        break;
                    default:
                        break;
                }
            }
        }

    }

}
