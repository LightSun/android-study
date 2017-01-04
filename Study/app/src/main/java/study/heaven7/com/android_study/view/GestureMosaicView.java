package study.heaven7.com.android_study.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import study.heaven7.com.android_study.helper.GestureHelper;


/**
 * 马赛克view
 */
public class GestureMosaicView extends View {

    private static final String TAG = "GestureMosaicView";
    private final GestureHelper mGestureHelper;

    public GestureMosaicView(Context context) {
        this(context, null, 0);
    }

    public GestureMosaicView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureMosaicView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mGestureHelper = new GestureHelper(this);
    }

    public void setImageFilePath(String absPath){
        mGestureHelper.setSrcPath(absPath);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        //return controller.onTouch(this, event);
        return mGestureHelper.onTouchEvent(event) || super.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        mGestureHelper.onSizeChanged(width, height, oldWidth, oldHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mGestureHelper.onDraw(canvas);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        mGestureHelper.computeScroll();
    }
}


