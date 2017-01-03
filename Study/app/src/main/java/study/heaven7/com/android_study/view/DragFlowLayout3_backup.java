package study.heaven7.com.android_study.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import study.heaven7.com.android_study.alert_window.AlertWindowHelper;
import study.heaven7.com.android_study.util.ViewUtils;

/**
 * Created by heaven7 on 2016/8/1.
 */
public class DragFlowLayout3_backup extends FlowLayout {

    private static final String TAG = "DragGridLayout";

    private static final int INVALID_INDXE = -1;
    private static final boolean DEBUG = true;

    private static final Comparator<Item> sComparator = new Comparator<Item>() {
        @Override
        public int compare(Item lhs, Item rhs) {
            return compareImpl(lhs.index, rhs.index);
        }
        public int compareImpl(int lhs, int rhs) {
            return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
        }
    };

    private final ItemManager mItemManager = new ItemManager();
    private AlertWindowHelper mWindomHelper;
    private Callback mCallback;

    private boolean mDispatchToAlertWindow;
    private int[] mTempLocation  = new int[2];

    private final AlertWindowHelper.ICallback mWindowCallback = new AlertWindowHelper.ICallback() {
        @Override
        public void onCancel(View view, MotionEvent event) {
            //infoWhenDebug("onCancel","------------->");
            releaseDrag();
        }
        @Override
        public boolean onMove(View view, MotionEvent event) {
            //infoWhenDebug("onMove","------------->");
            return onMoveImpl(view);
        }
    };
    private boolean mInDragState;

    private boolean mHasPerformedLongPress;
    private CheckForTap mCheckForTap;
    private CheckForLongPress mCheckForLongPress;
    private GestureDetectorCompat mGestureDetector;
    private View mTouchChild;

    public static abstract class Callback {

        public abstract void onStateChanged(View view, boolean dragState);
        @NonNull
        public abstract View copyChildView(View v, int index);

        public abstract void bindToWindowView(View windowView, View child);

        public abstract View createWindowView(View touchChildView);

        /**
         * Called to determine the Z-order of child views.
         *
         * @param index the ordered position to query for
         * @return index of the view that should be ordered at position <code>index</code>
         */
        public int getOrderedChildIndex(int index) {
            return index;
        }

        /**
         * perform the click event if you need. and return true if you performed the click event.
         * @param dragFlowLayout the DragFlowLayout
         * @param child the direct child of DragFlowLayout.
         * @param event the event of trigger this click event
         * @param inDragState indicate current whether is in drag state or not.
         * @return true,if you performed the click event
         */
        public abstract boolean performClick(DragFlowLayout3_backup dragFlowLayout, View child, MotionEvent event, boolean inDragState);
    }

    public DragFlowLayout3_backup(Context context) {
        this(context,null);
    }

    public DragFlowLayout3_backup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public DragFlowLayout3_backup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    @TargetApi(21)
    public DragFlowLayout3_backup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mWindomHelper = new AlertWindowHelper(context);
        mGestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
               // infoWhenDebug("mGestureDetector_onDown","----------------- > mTouchChild = " + mTouchChild);
                mTouchChild = findTopChildUnder((int) e.getX(), (int) e.getY());
               // infoWhenDebug("mGestureDetector_onDown","----------------- > after find : mTouchChild = " + mTouchChild);
                if(mTouchChild!=null && !mDispatchToAlertWindow && mInDragState){
                    beginDrag(mTouchChild);
                }
                return mTouchChild != null;
            }
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
               // infoWhenDebug("mGestureDetector_onSingleTapUp","----------------- >");
                return mCallback.performClick(DragFlowLayout3_backup.this, mTouchChild, e , mInDragState);
            }
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                final boolean handled = mWindomHelper.getView() != null && mWindomHelper.getView().dispatchTouchEvent(e2);
              //  infoWhenDebug("mGestureDetector_onScroll","----------------- > handled = "+ handled);
                return handled;
            }
            @Override
            public void onLongPress(MotionEvent e) {
                infoWhenDebug("mGestureDetector_onLongPress","----------------- >");
                if(!mInDragState) {
                    showOrHideState(true, false);
                    beginDrag(mTouchChild);
                }else{
                    if(mTouchChild != null){
                        beginDrag(mTouchChild);
                    }
                }
            }
        });
    }

    public void setDragCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }
    public void beginDrag(View childView) {
        if(childView == null){
            throw new NullPointerException();
        }
        childView.setVisibility(View.INVISIBLE);
        mDispatchToAlertWindow = true;
        mItemManager.findDragItem(childView);
        //childView.getLocationInWindow(mTempLocation);
        childView.getLocationOnScreen(mTempLocation);
        mWindomHelper.showView(mCallback.createWindowView(childView), mTempLocation[0], mTempLocation[1], true, mWindowCallback);
    }
    private boolean onMoveImpl(View view) {
        final List<Item> mItems = mItemManager.mItems;
        Item item = null;
        int centerX, centerY;
        boolean found = false;
        for(int i=0, size = mItems.size() ; i < size ; i++){
            item = mItems.get(i);
            item.view.getLocationOnScreen(mTempLocation);
            centerX = mTempLocation[0] + item.view.getWidth()/2;
            centerY = mTempLocation[1] + item.view.getHeight()/2;
            // infoWhenDebug("onMove_checkView","centerX = " + centerX + " ,centerY = "+centerY );
            if(isViewUnderInScreen(view, centerX, centerY, false)  && item != mItemManager.mDragItem){
                infoWhenDebug("onMove_isViewUnderInScreen","index = " + item.index );
                /**
                 * Drag到target目标的center时，判断有没有已经hold item, 有的话，先删除旧的,
                 */
                found = true;
                break;
            }
        }
        /** fix bug
         08-04 20:25:08.123 25653-25653/study.heaven7.com.android_study I/DragGridLayout: called [ onMove() ]: ------------->
         08-04 20:25:08.124 25653-25653/study.heaven7.com.android_study I/DragGridLayout: called [ onMove_isViewUnderInWindow() ]: index = 12
         08-04 20:25:08.125 25653-25653/study.heaven7.com.android_study D/DragGridLayout: called [ onRemoveView() ]: targetIndex = 11
         */
        if(found ){
            //the really index to add
            final int index = item.index;
            Item dragItem = mItemManager.mDragItem;
            removeView(mItemManager.mDragItem.view);
            //add hold
            View hold = mCallback.copyChildView(dragItem.view, dragItem.index);
            hold.setVisibility(View.INVISIBLE);  //隐藏
            addView(hold, index);
            //reset drag item and alert view
            mItemManager.findDragItem(hold);
            mCallback.bindToWindowView(mWindomHelper.getView(), mItemManager.mDragItem.view);
            infoWhenDebug("onMove","hold index = " + mItemManager.mDragItem.index);
        }
        return found;
    }

    public void releaseDrag() {
        if(mItemManager.mDragItem!=null) {
            mItemManager.mDragItem.view.setVisibility(View.VISIBLE);
            mCallback.onStateChanged(mItemManager.mDragItem.view, mInDragState);
        }
        mWindomHelper.releaseView();
        mTouchChild = null;
    }

    /**
     * @param dragState 是否拖拽状态，比如拖拽时该显示关闭按钮
     * @param showChildren 是否显示当前view的所有隐藏child(直接child，非递归)
     */
    public void showOrHideState(boolean dragState, boolean showChildren){
        this.mInDragState = dragState;
        final Callback mCallback = this.mCallback;
        View view;
        for(int i=0, size = getChildCount(); i < size ;i++){
            view = getChildAt(i);
            if(showChildren && view.getVisibility() != View.VISIBLE){
                view.setVisibility(View.VISIBLE);
            }
            mCallback.onStateChanged(view, dragState);
        }
    }

    /**
     * Find the topmost child under the given point within the parent view's coordinate system.
     * The child order is determined using {@link Callback#getOrderedChildIndex(int)}.
     *
     * @param x X position to test in the parent's coordinate system
     * @param y Y position to test in the parent's coordinate system
     * @return The topmost child view under (x, y) or null if none found.
     */
    public View findTopChildUnder(int x, int y) {
        final int childCount = getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            final View child = getChildAt(mCallback.getOrderedChildIndex(i));
            if (ViewUtils.isViewIntersect(x, y, child))
                return child;
        }
        return null;
    }

    private boolean isViewUnderInScreen(View view, int x, int y, boolean log) {
        if (view == null) {
            return false;
        }
        int w = view.getWidth();
        int h = view.getHeight();
        view.getLocationOnScreen(mTempLocation);
        int viewX = mTempLocation[0];
        int viewY = mTempLocation[1];
        if(log) {
            infoWhenDebug("isViewUnderInScreen", String.format(Locale.getDefault(),
                    "viewX = %d ,viewY = %d ,width = %d ,height = %d", viewX, viewY, w, h));
        }
        return x >= viewX && x < viewX + w
                && y >= viewY && y < viewY + h;
    }
    //=================================== override method ===================================== //

    @Override
    public void addView(View child, int index, LayoutParams params) {
        super.addView(child, index, params);
        mItemManager.onAddView(child, index, params) ;
    }

    @Override
    public void removeViewAt(int index) {
        super.removeViewAt(index);
        mItemManager.onRemoveViewAt(index);
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        mItemManager.onRemoveView(view);
    }
    @Override
    public void removeAllViews() {
        super.removeAllViews();
        mItemManager.onRemoveAllViews();
    }

    /*
     * 事件处理： 必须消耗down事件才能接收接下来的事件
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
       // infoWhenDebug("dispatchTouchEvent", ev.toString());
       // infoWhenDebug("dispatchTouchEvent", "------> mDispatchToAlertWindow = " + mDispatchToAlertWindow +" ,mInDragState = " + mInDragState);
        //如果不在这里分发给alert window. alert再首次会接收不到事件 or addView后 alert window view接收不到事件
        /*
        if(mDispatchToAlertWindow) {
            if(mWindomHelper.getView()!=null) {
                mWindomHelper.getView().dispatchTouchEvent(ev);
            }
            //check cancel ?
            if(ev.getAction() == MotionEvent.ACTION_CANCEL || ev.getAction() == MotionEvent.ACTION_UP) {
                mDispatchToAlertWindow = false;
            }
            return false;
        }else{
            if(mInDragState ){
                View child = findTopChildUnder((int)ev.getX(),(int)ev.getY());
                if(child != null){
                    beginDrag(child);
                    return true ;//返回true,可拖拽，但是child无法接收事件了.
                }
            }
        }*/
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
       // infoWhenDebug("onInterceptTouchEvent", event.toString());
        /*final float x = event.getX();
        final float y = event.getY();
        final int action = event.getAction();

        boolean handled = false;
        if(isLongClickable() || isClickable()){
            switch (action){
                case MotionEvent.ACTION_DOWN:
                    mHasPerformedLongPress = false;

                    break;

                case MotionEvent.ACTION_MOVE:
                    break;

                case MotionEvent.ACTION_UP:
                    break;

                case MotionEvent.ACTION_CANCEL:
                    //reset
                    break;

            }
        }
        return handled;*/

        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
       // infoWhenDebug("onTouchEvent", event.toString());
        //infoWhenDebug("onTouchEvent", "------> mDispatchToAlertWindow = " + mDispatchToAlertWindow +" ,mInDragState = " + mInDragState);
        final boolean handled = mGestureDetector.onTouchEvent(event);
        if(mDispatchToAlertWindow){
            mWindomHelper.getView().dispatchTouchEvent(event);
            boolean canceled = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
            if(canceled){
                mDispatchToAlertWindow = false;
            }
        }
        return handled;
    }
    //=================================== end -- override method ===================================== //

    public static void infoWhenDebug(String method, String msg) {
        if(DEBUG) {
            Log.i(TAG, "called [ " + method + "() ]: " + msg);
        }
    }
    private static void debugWhenDebug(String method, String msg) {
        if(DEBUG) {
            Log.d(TAG, "called [ " + method + "() ]: " + msg);
        }
    }

    private final class CheckForTap implements Runnable {
        public float x;
        public float y;

        @Override
        public void run() {
            //setPressed(true, x, y);
            checkForLongClick(ViewConfiguration.getTapTimeout());
        }
    }
    private final class CheckForLongPress implements Runnable{
        @Override
        public void run() {
            if(getParent()!=null && performLongClick()){
                mHasPerformedLongPress = true;
            }
        }
    }

    private void checkForLongClick(int tapTimeout) {

    }

    private static class Item{
        int index;
        View view;

        @Override
        public String toString() {
            return "Item{" +
                    "index=" + index +
                    '}';
        }
    }

    private static class ItemManager {
        final List<Item> mItems = new ArrayList<>();
        /** 对应的拖拽item */
        Item mDragItem = null;

        public void onAddView(View child, int index, LayoutParams params) {
            index = index != -1 ? index : mItems.size();
            debugWhenDebug("onAddView", "index = " + index );
            Item item;
            for(int i=0,size = mItems.size() ;i<size ;i++){
                item =  mItems.get(i);
                if(item.index >= index){
                    item.index ++;
                }
            }
            //add
            item = new Item();
            item.index = index;
            item.view = child;
            mItems.add(item);
            Collections.sort(mItems, sComparator);
            //debugWhenDebug("onAddView",mItems.toString());
        }

        public void onRemoveViewAt(int index) {
            debugWhenDebug("onRemoveViewAt", "index = " + index );
            Item item;
            for(int i=0,size = mItems.size() ;i<size ;i++){
                item =  mItems.get(i);
                if(item.index > index){
                    item.index --;
                }
            }
            mItems.remove(index);
            Collections.sort(mItems, sComparator);
           // debugWhenDebug("onAddView",mItems.toString());
        }

        public void onRemoveView(View view) {
            Item item;
            int targetIndex = INVALID_INDXE;
            for(int i=0, size = mItems.size() ;i<size ;i++){
                item =  mItems.get(i);
                if(item.view == view){
                    targetIndex = item.index ;
                    break;
                }
            }
            debugWhenDebug("onRemoveView", "targetIndex = " + targetIndex );
            if(targetIndex == -1){
                throw new IllegalStateException("caused by targetIndex == -1");
            }
            // -- index if need
            for(int i=0,size = mItems.size() ;i<size ;i++){
                item =  mItems.get(i);
                if(item.index > targetIndex){
                    item.index --;
                }
            }
            mItems.remove(targetIndex);
            Collections.sort(mItems, sComparator);
            //debugWhenDebug("onAddView",mItems.toString());
        }
        public void onRemoveAllViews() {
            mItems.clear();
        }

        public void findDragItem(View touchView) {
            Item item;
            for(int i=0 ,size = mItems.size() ;i<size ;i++){
                item =  mItems.get(i);
                if(item.view == touchView){
                    mDragItem = item;
                    break;
                }
            }
        }
    }

}
