package study.heaven7.com.android_study.nestedScroll;

import android.content.Context;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import java.util.Arrays;

/**
 * 嵌套滑动框架学习: google.
 * 学习时，嵌套：NestedScrollingView.
 --------------子View开始滚动------------------
 ----父布局onStartNestedScroll----------------
 ----父布局onNestedScrollAccepted---------------
 -----------子View把总的滚动距离传给父布局--------
 ----父布局onNestedPreScroll----------------
 ---offset--x:0,offset--y:20
 -----------子View把剩余的滚动距离传给父布局-------
 ----父布局onNestedScroll----------------
 -----------子View停止滚动---------------
 ----父布局onStopNestedScroll----------------
 */
public class NestScrollingLayout extends FrameLayout implements NestedScrollingParent {

    private static final String TAG = "NestScrollingLayout";

    private NestedScrollingParentHelper mParentHelper;

    public NestScrollingLayout(Context context, AttributeSet attrs,
                               int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public NestScrollingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NestScrollingLayout(Context context) {
        super(context);
        init();

    }

    private void init() {
        mParentHelper = new NestedScrollingParentHelper(this);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {

        Log.d(TAG, "child==target:" + (child == target));

        Log.d(TAG, "----父布局onStartNestedScroll----------------");

        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {

        Log.d(TAG, "----父布局onNestedScrollAccepted---------------");

        mParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
    }

    @Override
    public void onStopNestedScroll(View target) {
        Log.d(TAG, "----父布局onStopNestedScroll----------------");
        mParentHelper.onStopNestedScroll(target);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed) {
        Log.d(TAG, "----父布局onNestedScroll----------------");
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        //scrollBy(0, -dy);

        consumed[0] = 0;
        consumed[1] = 0; // 把消费的距离放进去
        Log.d(TAG, "----parent: onNestedPreScroll---------------- consumed: " + Arrays.toString(consumed));
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        Log.d(TAG, "----父布局onNestedFling----------------");
        return true;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        Log.d(TAG, "----父布局onNestedPreFling----------------");
        return true;
    }

    @Override
    public int getNestedScrollAxes() {
        Log.d(TAG, "----父布局getNestedScrollAxes----------------");
        return mParentHelper.getNestedScrollAxes();
    }
}
