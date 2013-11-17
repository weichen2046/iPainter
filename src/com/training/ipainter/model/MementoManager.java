package com.training.ipainter.model;

import java.util.List;
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
    private List<IDrawable> mDrawingHistories;

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

    public void setDrawingHistories(List<IDrawable> histories) {
        mDrawingHistories = histories;
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
        Memento mem = mUndos.pop();
        switch (mem.mUndoableType) {
            case POSITION_CHANGE_UNDO_TYPE:
                PositionChangeMementoData data = (PositionChangeMementoData) mem.mData;
                mem.mDrawable.setBounds(data.mSrc);
                break;
            case CREATE_UNDO_TYPE:
                int index = Integer.valueOf(mem.mData.toString());
                // if (undoMem.mDrawable != mDrawingHistories.get(index)) {
                // // wrap by a SelectBorderDecorator
                // }
                mDrawingHistories.remove(index);
                break;
            default:
                break;
        }
        mRedos.push(mem);
    }

    public void redo() {
        Memento mem = mRedos.pop();
        switch (mem.mUndoableType) {
            case POSITION_CHANGE_UNDO_TYPE:
                PositionChangeMementoData data = (PositionChangeMementoData) mem.mData;
                mem.mDrawable.setBounds(data.mDest);
                break;
            case CREATE_UNDO_TYPE:
                int index = Integer.valueOf(mem.mData.toString());
                mDrawingHistories.add(index, mem.mDrawable);
                break;
            default:
                break;
        }
        mUndos.push(mem);
    }

    public void addPositionChangedMemento(IDrawable drawable) {
        Memento mem = new Memento();
        mem.mDrawable = drawable;
        mem.mUndoableType = POSITION_CHANGE_UNDO_TYPE;
        mem.mData = new PositionChangeMementoData(mPrevRect, mCurrentRect);
        mUndos.push(mem);
    }

    public void addCreationMemento(IDrawable drawable, int index) {
        Memento mem = new Memento();
        mem.mDrawable = drawable;
        mem.mUndoableType = CREATE_UNDO_TYPE;
        mem.mData = index;
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
