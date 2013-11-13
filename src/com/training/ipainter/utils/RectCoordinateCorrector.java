package com.training.ipainter.utils;

public class RectCoordinateCorrector {
    public int mLeft;
    public int mTop;
    public int mRight;
    public int mBottom;

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
    }
}
