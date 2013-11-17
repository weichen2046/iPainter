package com.training.ipainter.model;

import java.util.Stack;

import android.graphics.Rect;

public class MementoManager {

    public static final int POSITION_CHANGE_UNDO_TYPE = 1 << 1;
    public static final int SIZE_CHANGE_UNDO_TYPE = 1 << 2;
    public static final int PAINT_CHANGE_UNDO_TYPE = 1 << 3;
    public static final int CREATE_UNDO_TYPE = 1 << 4;
    public static final int DELETE_UNDO_TYPE = 1 << 5;
    public static final int COMPOSITE_UNDO_TYPE = 1 << 6;
    public static final int DECOMPOSITE_UNDO_TYPE = 1 << 7;

    private Rect mPrevRect;
    private Rect mCurrentRect;

    private Stack<Memento> mUndos = new Stack<Memento>();
    private Stack<Memento> mRedos = new Stack<Memento>();

    private MementoManager() {
        mPrevRect = new Rect();
        mCurrentRect = new Rect();
    }

    /**
     * Singleton pattern. Initialization-on-demand holder idiom. The original
     * implementation from Bill Pugh.
     */
    private static class LazyHolder {
        private static final MementoManager INSTANCE = new MementoManager();
    }

    public static MementoManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void setPrevRect(Rect rect) {
        mPrevRect.set(rect);
    }

    public void setCurrentRect(Rect rect) {
        mCurrentRect.set(rect);
    }

    public boolean isPositionChanged() {
        return !mPrevRect.equals(mCurrentRect);
    }

    public boolean canUndo() {
        return mUndos.size() > 0;
    }

    public boolean canRedo() {
        return mRedos.size() > 0;
    }

    public void undo() {
        Memento undoMem = mUndos.pop();
        switch (undoMem.mUndoableType) {
            case POSITION_CHANGE_UNDO_TYPE:
                PositionChangeMementoData data = (PositionChangeMementoData) undoMem.mData;
                undoMem.mDrawable.setBounds(data.mSrc);
                break;
            default:
                break;
        }
        mRedos.push(undoMem);
    }

    public void redo() {
        Memento redoMem = mRedos.pop();
        switch (redoMem.mUndoableType) {
            case POSITION_CHANGE_UNDO_TYPE:
                PositionChangeMementoData data = (PositionChangeMementoData) redoMem.mData;
                redoMem.mDrawable.setBounds(data.mDest);
                break;
            default:
                break;
        }
        mUndos.push(redoMem);
    }

    public void addPositionChangedMemento(IDrawable drawable) {
        Memento mem = new Memento();
        mem.mDrawable = drawable;
        mem.mUndoableType = POSITION_CHANGE_UNDO_TYPE;
        mem.mData = new PositionChangeMementoData(mPrevRect, mCurrentRect);
        mUndos.push(mem);
    }

    public class Memento {
        public IDrawable mDrawable;
        public int mUndoableType;
        public Object mData;
    }

    class PositionChangeMementoData {
        public Rect mSrc;
        public Rect mDest;

        public PositionChangeMementoData(Rect src, Rect dest) {
            mSrc = new Rect();
            mSrc.set(src);
            mDest = new Rect();
            mDest.set(dest);
        }
    }
}
