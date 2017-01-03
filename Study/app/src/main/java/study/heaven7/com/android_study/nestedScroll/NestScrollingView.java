package study.heaven7.com.android_study.nestedScroll;

import android.content.Context;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class NestScrollingView extends FrameLayout implements NestedScrollingChild {

    private static final String TAG = "NestScrollingView";

    private NestedScrollingChildHelper mChildHelper;

    private int[] mConsumed = new int[2];
    private int[] mOffset = new int[2];
    private int mLastY;

    public NestScrollingView(Context context, AttributeSet attrs,
                             int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public NestScrollingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NestScrollingView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        Log.d(TAG, "-----------子View开始滚动---------------");
        return mChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        Log.d(TAG, "-----------子View停止滚动---------------");
        mChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed,
                                        int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        Log.d(TAG, "-----------子View把剩余的滚动距离传给父布局---------------");

        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        Log.d(TAG, "-----------子View把总的滚动距离传给父布局---------------");
        return mChildHelper.dispatchNestedPreScroll(dx, dy,
                consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY,
                consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        /**
         *  // 参数dx: 表示view本次x方向的滚动的总距离长度
         // 参数dy: 表示view本次y方向的滚动的总距离长度
         // 参数consumed: 表示父布局消费的距离,consumed[0]表示x方向,consumed[1]表示y方向
         // 参数offsetInWindow: 表示剩下的距离dxUnconsumed和dyUnconsumed使得view在父布局中的位置偏移了多少
         public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow);

         // 参数dxConsumed: 表示view消费了x方向的距离长度
         // 参数dyConsumed: 表示view消费了y方向的距离长度
         // 参数dxUnconsumed: 表示滚动产生的x滚动距离还剩下多少没有消费
         // 参数dyUnconsumed: 表示滚动产生的y滚动距离还剩下多少没有消费
         // 参数offsetInWindow: 表示剩下的距离dxUnconsumed和dyUnconsumed使得view在父布局中的位置偏移了多少
         public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed,
         int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow);
         */
        final int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                // 按下事件调用startNestedScroll
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                break;

            case MotionEvent.ACTION_MOVE:
                int dy = mLastY - y;
                mLastY = y;
                // 移动事件调用startNestedScroll
                dispatchNestedPreScroll(0, dy, mConsumed, mOffset);
                // 输出一下偏移
                Log.d(TAG, "offset--x:" + mOffset[0] + ",offset--y:" + mOffset[1]);
                Log.d(TAG, "mConsumed--x:" + mConsumed[0] + ",offset--y:" + mConsumed[1]);
                dispatchNestedScroll(0, 0, 0, 0, mOffset);
                break;

            case MotionEvent.ACTION_UP:
                // 弹起事件调用startNestedScroll
                stopNestedScroll();
                break;
            default:
                break;
        }
        return true;
    }
}
