package com.training.ipainter.core;

import com.training.ipainter.model.IDrawable;

class Signal {

    public int mX;
    public int mY;
    public int mSignalType;
    public IDrawable mGraphicObj;

    public static final int SIG_DOWN = 1;
    public static final int SIG_UP = 2;
    public static final int SIG_MOVE = 3;

    public static Signal obtain() {
        // current we just return a new Signal object
        // TODO need refactoring, don't always new Signal object
        // we can reuse these object like Message, we can recycle it when
        // PainterThread done with it.
        return new Signal();
    }

    public void setPoint(int x, int y) {
        mX = x;
        mY = y;
    }

    public void setSignalType(int type) {
        mSignalType = type;
    }

    public void setDrawable(IDrawable graphicObj) {
        mGraphicObj = graphicObj;
    }

}
