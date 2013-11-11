/**
 * 
 */
package com.training.ipainter.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.training.ipainter.R;
import com.training.ipainter.drawingtools.DrawingToolsManager;

/**
 *
 */
public class PainterActivity extends Activity {

    private static final String TAG = "PainterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, Menu.FIRST + 1, 1, "模式切换");
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = (MenuItem) menu.findItem(Menu.FIRST + 1);
        if (menu != null) {
            DrawingToolsManager tools = DrawingToolsManager.getInstance();
            if(tools.getMode() == DrawingToolsManager.SELECT_MODE) {
                item.setTitle("绘画模式");
            } else {
                item.setTitle("选择模式");
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Menu.FIRST + 1:
                DrawingToolsManager tools = DrawingToolsManager.getInstance();
                if (tools.getMode() == DrawingToolsManager.SELECT_MODE) {
                    tools.setMode(DrawingToolsManager.PAINT_MODE);
                    Log.d(TAG, "Change mode to paint mode.");
                } else {
                    tools.setMode(DrawingToolsManager.SELECT_MODE);
                    Log.d(TAG, "Change mode to select mode.");
                }
                break;
            default:
                break;
        }
        return true;
    }

}
