package study.heaven7.com.android_study.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by heaven7 on 2017/6/15 0015.
 */

public class ShadowImageView extends View implements NestedScrollingChild{

    private static final String TAG = "ShadowImageView";

    private Drawable mContentDrawable;
    private Drawable mShadowDrawable;
    private int mPosX;
    private int mPosy;
    private int mScrollPointerId;

    private int mLastTouchX;
    private int mLastTouchY;
    private int mInitialTouchX;
    private int mInitialTouchY;

    private static final byte DIR_LEFT   = 1;
    private static final byte DIR_RIGHT  = 2;
    private static final byte DIR_UP    = 3;
    private static final byte DIR_DOWN = 4;  
    private static final byte DIR_LEFT_UP          = 5;
    private static final byte DIR_LEFT_DOWN       = 6;
    private static final byte DIR_RIGHT_UP         = 7;
    private static final byte DIR_RIGHT_DOWN      = 8;
    private static final byte DIR_STAY = 9;


    public ShadowImageView(Context context) {
        super(context);
        init(context);
    }

    public ShadowImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShadowImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
    }

    public void setContentResource(int id) {
        mContentDrawable = new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), id));
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mPosX = getMeasuredWidth() / 2;
        mPosy = getMeasuredHeight() / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = mContentDrawable.getIntrinsicWidth();
        int height = mContentDrawable.getIntrinsicHeight();
        mContentDrawable.setBounds(mPosX, mPosy, mPosX + width - 1, mPosy + height - 1);
        mContentDrawable.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        final int action = MotionEventCompat.getActionMasked(event);
        final int actionIndex = MotionEventCompat.getActionIndex(event);

        switch (action) {

            case MotionEvent.ACTION_DOWN: {
                mScrollPointerId = event.getPointerId(0);
                mInitialTouchX = mLastTouchX = (int) (event.getX() + 0.5f);
                mInitialTouchY = mLastTouchY = (int) (event.getY() + 0.5f);
            }
            break;

            case MotionEventCompat.ACTION_POINTER_DOWN: {
                mScrollPointerId = event.getPointerId(actionIndex);
                mInitialTouchX = mLastTouchX = (int) (event.getX(actionIndex) + 0.5f);
                mInitialTouchY = mLastTouchY = (int) (event.getY(actionIndex) + 0.5f);
            }
            break;

            case MotionEvent.ACTION_MOVE: {
                //we should follow the nested standard of Google.
                final int index = event.findPointerIndex(mScrollPointerId);
                if (index < 0) {
                    Log.e(TAG, "Error processing scroll; pointer index for id " +
                            mScrollPointerId + " not found. Did any MotionEvents get skipped?");
                    return false;
                }
                final int x = (int) (event.getX(index) + 0.5f);
                final int y = (int) (event.getY(index) + 0.5f);
                final int dx = x - mLastTouchX;
                final int dy = y - mLastTouchY;
                mLastTouchX = x;
                mLastTouchY = y;
                mPosX += dx;
                mPosy += dy;
                invalidate();
            }
            break;

            case MotionEventCompat.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                mPosX = getMeasuredWidth() / 2;
                mPosy = getMeasuredHeight() / 2;
                invalidate();
                break;
        }
        return true;
    }

    private static int calculateDirection(int lastX, int lastY ,int dx, int dy){
        if(dx == 0){
            if(dy == 0){
                return DIR_STAY;
            }
            return dy > 0 ? DIR_DOWN : DIR_UP;
        }
        if(dy == 0){
            return dx > 0 ? DIR_RIGHT : DIR_LEFT;
        }


        return DIR_DOWN;
    }

}
