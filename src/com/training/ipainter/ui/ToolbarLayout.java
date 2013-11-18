package com.training.ipainter.ui;


import com.training.ipainter.R;
import com.training.ipainter.drawingtools.DrawingToolsManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.GestureDetector.OnGestureListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.Button;;

public class ToolbarLayout extends LinearLayout implements View.OnClickListener,ColorPickerDialog.OnColorChangedListener{
    private Scroller scroller;
    private DisplayMetrics dm = getResources().getDisplayMetrics();
    private int currentScreenIndex;
    private GestureDetector gestureDetector;
    private boolean active; //true：边界能滑动出工具栏
    private boolean isOutside = false; //true: 工具栏已滑出
    private int menuWidth = 0;
    private int minLength = 50;
    private DrawingToolsManager mToolsManager;
    private Handler mHander4Parent;
    private PaintBoardView paintboard;
    private ColorPickerDialog colorPicker;
    private Paint mPaint;
    public void setHandler(Handler handler) {
        mHander4Parent = handler;
    }

    public ToolbarLayout(Context context) {
        super(context);
        Log.v("TAG","1");
        initView(context);
       
    }
    public ToolbarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.v("TAG","2");
        initView(context);
    }
    private OnScrollSideChangedListener scrollSideChangedListener;

    public Scroller getScroller(){
        return scroller;
    }

    public OnScrollSideChangedListener getScrollSideChangedListener(){
        return scrollSideChangedListener;
    }

    public void setScrollSideChangedListener(
            OnScrollSideChangedListener scrollSideChangedListener){
        this.scrollSideChangedListener = scrollSideChangedListener;
    }
    @SuppressWarnings("deprecation")
    private void initView(final Context context){
        
        mToolsManager = DrawingToolsManager.getInstance();
        this.scroller = new Scroller(context,AnimationUtils.loadInterpolator(context,
                android.R.anim.overshoot_interpolator));

        colorPicker = new ColorPickerDialog(context, Color.BLACK, "color", this);
        mPaint = mToolsManager.getPaint();
        this.gestureDetector = new GestureDetector(new OnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e){
                return false;
            }
            @Override
            public void onShowPress(MotionEvent e) {

            }
            @Override
            public boolean onScroll(MotionEvent e1,MotionEvent e2,float distanceX, float distanceY){
                
                if(active == true){
                    Log.v("TAG","scroll");
                    Log.v("TAG","distanceX"+Float.toString(distanceX));
                    scrollBy((int) distanceX, 0);
                    }
                return true;
            }
            @Override
            public void onLongPress(MotionEvent e) {

            }
            @Override
            public boolean onFling(MotionEvent e1,MotionEvent e2,float velocityX, float velocityY){
                if (Math.abs(velocityX) > ViewConfiguration.get(context).getScaledMinimumFlingVelocity()){
                    Log.v("TAG", "on scroll>>>>>>>>>>>>>>>>>滑动<<<<<<<<<<<<<<>>>");
                }
                return true;
            }
            @Override
            public boolean onDown(MotionEvent e){
                return false;
            }
        });
    }


    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()){
            Log.v("TAG",">>>>>>>>>>computeScroll>>>>>"+scroller.getCurrX());
            scrollTo(scroller.getCurrX(),0);
            postInvalidate();
        }else{
            scrollTo(scroller.getCurrX(),0);
            postInvalidate();
        }
        super.computeScroll();
    }
    private static final int select_mode = 3;
    private static final int pick_color = 4;
    private static final int composition = 5;
    private static final int decomposition = 6;
    private static final int undo = 7;
    private static final int redo = 8;
    @SuppressLint("NewApi")
    @Override
    protected void onFinishInflate(){
        
        ToolbarLayout mScrollLayout=(ToolbarLayout)findViewById(R.id.my_scrollLayout);
        Button BrushLine = (Button)mScrollLayout.findViewById(R.id.brushLine);
        BrushLine.setOnClickListener(this);
        BrushLine.setTag(DrawingToolsManager.BRUSH_LINE);
        Button BrushRect = (Button)mScrollLayout.findViewById(R.id.brushRect);
        BrushRect.setOnClickListener(this);
        BrushRect.setTag(DrawingToolsManager.BRUSH_RECT);
        
        Button BrushCircle = (Button)mScrollLayout.findViewById(R.id.brushCircle);
        BrushCircle.setOnClickListener(this);
        BrushCircle.setTag(DrawingToolsManager.BRUSH_CIRCLE);
        Button mode = (Button)mScrollLayout.findViewById(R.id.mode);
        mode.setOnClickListener(this);
        mode.setTag(select_mode);
        
        Button colorPicker = (Button)mScrollLayout.findViewById(R.id.colorPicker);
        colorPicker.setOnClickListener(this);
        colorPicker.setTag(pick_color);
        
        Button composite = (Button)mScrollLayout.findViewById(R.id.composite);
        composite.setOnClickListener(this);
        composite.setTag(composition);
        
        Button decomposite = (Button)mScrollLayout.findViewById(R.id.decomposite);
        decomposite.setOnClickListener(this);
        decomposite.setTag(decomposition);
      
        
        
        Button undo_bt = (Button)mScrollLayout.findViewById(R.id.undo);
        undo_bt.setOnClickListener(this);
        undo_bt.setTag(undo);
        
        Button redo_bt = (Button)mScrollLayout.findViewById(R.id.redo);
        redo_bt.setOnClickListener(this);
        redo_bt.setTag(redo);
    }

    private void touchStart(float x, float y) {
        Log.v("TAG","toolbar is touched");
        if( (dm.widthPixels-x) < minLength && isOutside == false){
            active = true;//toolbar can be slided out
            Log.v("TAG","go here");
        }
        if(isOutside){
            Log.v("TAG","scrollToScreen(2);");
            scrollToScreen(2);
            Message msg = mHander4Parent.obtainMessage(PainterActivity.TOOLS_TO_BACK);
            mHander4Parent.sendMessage(msg);
        }
    }

    private void touchMove(float x, float y) {
        if(active == true && (dm.widthPixels-x) < minLength){
            active = false;
            Log.v("TAG","scrollToScreen(1)");
            scrollToScreen(1);
        }
    }

    private void touchUp(float x, float y) {
        active = false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {;
        float x = event.getX();
        float y = event.getY();
        gestureDetector.onTouchEvent(event);
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                touchStart(x,y);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x,y);
                break;
            case MotionEvent.ACTION_UP:
                touchUp(x,y);
                break;
            default:
                break;
        }
        return true;
    }

    public void scrollToScreen(int whichScreen) {
        if (getFocusedChild() != null && whichScreen != currentScreenIndex
                && getFocusedChild() == getChildAt(currentScreenIndex)) {
            getFocusedChild().clearFocus();
        }
        int delta = 0;
        if(whichScreen==0)
            //TODO
            ;
        else if(whichScreen==1){ //滑出工具栏
            delta = minLength*4-getScrollX();
            isOutside = true;
        }
        else if(whichScreen==2){//缩回工具栏
            delta = -getScrollX();
            isOutside = false;
        }
        else
            return;

        Log.v("TAG","startScroll"+Integer.toString(whichScreen));
        scroller.startScroll(getScrollX(), 0, delta, 0, 500);
        invalidate();
        currentScreenIndex = whichScreen;
     }
    public interface OnScrollSideChangedListener{
        public void onScrollSideChanged(View v,boolean leftSide);
    }
    @Override
    public void onClick(View v) {
        int tag = (Integer)v.getTag();
        switch(tag){
            case DrawingToolsManager.BRUSH_LINE:
                Log.v("TAG","line");
                mToolsManager.setBrushType(DrawingToolsManager.BRUSH_LINE);
                mToolsManager.setMode(DrawingToolsManager.PAINT_MODE);
                clearFocus();
                break;
            case DrawingToolsManager.BRUSH_RECT:
                Log.v("TAG","rect");clearFocus();
                mToolsManager.setBrushType(DrawingToolsManager.BRUSH_RECT);
                mToolsManager.setMode(DrawingToolsManager.PAINT_MODE);
                break;
            case DrawingToolsManager.BRUSH_CIRCLE:
                Log.v("TAG","circle");clearFocus();
                mToolsManager.setBrushType(DrawingToolsManager.BRUSH_CIRCLE);
                mToolsManager.setMode(DrawingToolsManager.PAINT_MODE);
                break;
            case select_mode:
                Log.v("TAG","mode");
                    mToolsManager.setMode(DrawingToolsManager.SELECT_MODE);
                break;
            case pick_color:
                Log.v("TAG","pick color");
                colorPicker.show();
                break;
            case composition:
                if(mToolsManager.getComposableStatus() == 
                    DrawingToolsManager.COMPOSABLE_DECOMPOSABLE_BOTH_DISABLE)
                    ;
                else
                    mToolsManager.sendActions(DrawingToolsManager.COMPOSITE_ACTION);
                break;
            case decomposition:
                if (mToolsManager.getComposableStatus() ==
                    DrawingToolsManager.COMPOSABLE_DECOMPOSABLE_BOTH_DISABLE)
                    ;
                else
                    mToolsManager.sendActions(DrawingToolsManager.DECOMPOSITE_ACTION);
                break;
            case undo:
                mToolsManager.sendActions(DrawingToolsManager.UNDO_ACTION);
                break;
            case redo:
                mToolsManager.sendActions(DrawingToolsManager.REDO_ACTION);
                break;
            default:
                Log.v("TAG","other");
                return;
        }
        Log.v("TAG","scrollToScreen(2)");
        scrollToScreen(2);
        Message msg = mHander4Parent.obtainMessage(PainterActivity.TOOLS_TO_BACK);
        mHander4Parent.sendMessage(msg);
    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        /**
         *  设置布局，将子视图顺序横屏排列
         **/
        super.onLayout(changed, left, top, right, bottom);
        int move=getWidth()-menuWidth;
        for (int i = 0; i < getChildCount(); i++)
        {
            View child = getChildAt(i);
            child.layout(child.getLeft()+move,child.getTop(),child.getRight()+move,child.getBottom());
        }
    }

    @Override
    public void colorChanged(int color) {
        mPaint.setColor(color);
        mToolsManager.setPaint(mPaint);
        
    }
}