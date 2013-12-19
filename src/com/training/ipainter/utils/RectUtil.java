package com.training.ipainter.utils;

import android.graphics.Rect;
import android.graphics.RectF;

public class RectUtil {
    public int mLeft;
    public int mTop;
    public int mRight;
    public int mBottom;

    public Rect mRect = new Rect();
    public RectF mRectF = new RectF();

    public void rectifyCoordinate(int sX, int sY, int eX, int eY) {
        if (sX > eX) {
            sX = sX ^ eX;
            eX = sX ^ eX;
            sX = sX ^ eX;
        }
        if (sY > eY) {
            sY = sY ^ eY;
            eY = sY ^ eY;
            sY = sY ^ eY;
        }
        mLeft = sX;
        mTop = sY;
        mRight = eX;
        mBottom = eY;

        mRect.set(mLeft, mTop, mRight, mBottom);
        mRectF.set(mLeft, mTop, mRight, mBottom);
    }

    public static void rectifyCoordinate(Rect src, int sX, int sY, int eX, int eY) {
        if (sX > eX) {
            sX = sX ^ eX;
            eX = sX ^ eX;
            sX = sX ^ eX;
        }
        if (sY > eY) {
            sY = sY ^ eY;
            eY = sY ^ eY;
            sY = sY ^ eY;
        }
        src.set(sX, sY, eX, eY);
    }

    /**
     * return a rect that left always not large than right and top always not
     * large than bottom.
     * 
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @return
     */
    public static Rect getNormalRect(int sX, int sY, int eX, int eY) {
        Rect rect = new Rect();
        RectUtil.rectifyCoordinate(rect, sX, sY, eX, eY);

        return rect;
    }

    public RectUtil inflate(int x, int y) {
        mLeft -= x;
        mTop -= y;
        mRight += x;
        mBottom += y;

        mRect.set(mLeft, mTop, mRight, mBottom);
        mRectF.set(mLeft, mTop, mRight, mBottom);

        return this;
    }

    public static Rect inflate(Rect src, int x, int y) {
        return new Rect(src.left - x,
                src.top - y,
                src.right + x,
                src.bottom + y);
    }

    public static RectF rectToRectF(Rect src) {
        return new RectF(src);
    }

    public static void truncRectFToRect(RectF src, Rect des) {
        // sanity check
        if (src == null || des == null) {
            return;
        }
        des.set((int)src.left, (int)src.top, (int)src.right, (int)src.bottom);
    }
}
