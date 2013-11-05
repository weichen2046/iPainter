/**
 * 
 */
package com.training.ipainter.drawingtools;

/**
 *
 */
public class DrawingToolsManager {

    private DrawingToolsManager() {
    }

    /**
     * Singleton pattern. Initialization-on-demand holder idiom. The original
     * implementation from Bill Pugh.
     * 
     */
    private static class LazyHolder {
        private static final DrawingToolsManager INSTANCE = new DrawingToolsManager();
    }

    public static DrawingToolsManager getInstance() {
        return LazyHolder.INSTANCE;
    }

}
