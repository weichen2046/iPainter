/**
 * 
 */
package com.training.ipainter.ui;

import android.app.Activity;
import android.os.Bundle;

import com.training.ipainter.R;

/**
 *
 */
public class PainterActivity extends Activity {

    private BoardCanvas mCanvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        mCanvas = (BoardCanvas) findViewById(R.id.canvas);
    }

}
