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

    private static final int MODE_CHANGE_MENU = Menu.FIRST + 1;
    private static final int COMPOSITE_MENU = Menu.FIRST + 2;
    private static final int UNDO_MENU = Menu.FIRST + 3;
    private static final int REDO_MENU = Menu.FIRST + 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MODE_CHANGE_MENU, 1, R.string.mode_select);
        menu.add(Menu.NONE, COMPOSITE_MENU, 2, R.string.composite);
        menu.add(Menu.NONE, UNDO_MENU, 3, R.string.undo);
        menu.add(Menu.NONE, REDO_MENU, 3, R.string.redo);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        DrawingToolsManager tools = DrawingToolsManager.getInstance();
        MenuItem item = (MenuItem) menu.findItem(MODE_CHANGE_MENU);
        if (menu != null) {
            if(tools.getMode() == DrawingToolsManager.SELECT_MODE) {
                item.setTitle(R.string.paint_mode);
            } else {
                item.setTitle(R.string.select_mode);
            }
        }

        item = (MenuItem) menu.findItem(COMPOSITE_MENU);
        switch (tools.getComposableStatus()) {
        case DrawingToolsManager.COMPOSABLE_DECOMPOSABLE_BOTH_DISABLE:
            item.setTitle(R.string.composable_not_available);
            item.setEnabled(false);
            break;
        case DrawingToolsManager.COMPOSABLE_ENABLE:
            item.setTitle(R.string.composite);
            item.setEnabled(true);
            break;
        case DrawingToolsManager.DECOMPOSABLE_ENABLE:
            item.setTitle(R.string.decomposite);
            item.setEnabled(true);
            break;
        default:
            Log.d(TAG,
                    "Unknow composable status, " + tools.getComposableStatus());
            break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DrawingToolsManager tools = DrawingToolsManager.getInstance();
        switch (item.getItemId()) {
        case MODE_CHANGE_MENU:
                if (tools.getMode() == DrawingToolsManager.SELECT_MODE) {
                    tools.setMode(DrawingToolsManager.PAINT_MODE);
                    Log.d(TAG, "Change mode to paint mode.");
                } else {
                    tools.setMode(DrawingToolsManager.SELECT_MODE);
                    Log.d(TAG, "Change mode to select mode.");
                }
                break;
        case COMPOSITE_MENU:
            if (tools.getComposableStatus() == DrawingToolsManager.COMPOSABLE_ENABLE) {
                tools.sendActions(DrawingToolsManager.COMPOSITE_ACTION);
            } else {
                tools.sendActions(DrawingToolsManager.DECOMPOSITE_ACTION);
            }
            break;
        case UNDO_MENU:
            tools.sendActions(DrawingToolsManager.UNDO_ACTION);
            break;
        case REDO_MENU:
            tools.sendActions(DrawingToolsManager.REDO_ACTION);
            break;
        default:
                break;
        }
        return true;
    }

}
