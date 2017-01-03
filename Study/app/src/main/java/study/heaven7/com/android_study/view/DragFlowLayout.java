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
public class DragFlowLayout extends FlowLayout {

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
            releaseDragInternal();
        }
        @Override
        public boolean onMove(View view, MotionEvent event) {
            //infoWhenDebug("onMove","------------->");
            return onMoveImpl(view);
        }
    };
    /** is drag state */
    private boolean mIsDragState;

    private GestureDetectorCompat mGestureDetector;
    private View mTouchChild;

    public static abstract class Callback {

        public abstract void setChildByDragState(View child, boolean isDragState);

        @NonNull
        public abstract View copyChildView(View child, int index);


        public abstract void setWindowViewByChild(View windowView, View child);


        public abstract View createWindowView(View child);

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
         * called when a click event occurrence ,perform the click event if you need. and return true if you performed the click event.
         * @param dragFlowLayout the DragFlowLayout
         * @param child the direct child of DragFlowLayout.
         * @param event the event of trigger this click event
         * @param inDragState indicate current whether is in drag state or not.
         * @return true,if you performed the click event
         */
        public abstract boolean performClick(DragFlowLayout dragFlowLayout, View child, MotionEvent event, boolean inDragState);

        /**
         * is the child draggable
         * @param child the direct child of DragFlowLayout
         * @return true if the child is draggable
         */
        public boolean isChildDraggable(View child) {
            return true;
        }
    }

    public DragFlowLayout(Context context) {
        this(context,null);
    }

    public DragFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public DragFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    @TargetApi(21)
    public DragFlowLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mWindomHelper = new AlertWindowHelper(context);
        mGestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                mTouchChild = findTopChildUnder((int) e.getX(), (int) e.getY());
                infoWhenDebug("mGestureDetector_onDown","----------------- > after find : mTouchChild = " + mTouchChild);
               // boolean mDraggable = mTouchChild != null && mCallback.isChildDraggable(mTouchChild);
                if(mTouchChild!=null && !mDispatchToAlertWindow && mIsDragState){
                    if(mCallback.isChildDraggable(mTouchChild)) {
                        beginDrag(mTouchChild);
                    }
                }
                return mTouchChild != null;
            }
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                infoWhenDebug("mGestureDetector_onSingleTapUp","----------------- >");
                checkCallback();
                return mCallback.performClick(DragFlowLayout.this, mTouchChild, e , mIsDragState);
            }
            @Override
            public void onLongPress(MotionEvent e) {
                infoWhenDebug("mGestureDetector_onLongPress","----------------- >");
                if(!mIsDragState && mCallback.isChildDraggable(mTouchChild)) {
                    setDragState(true, false);
                    beginDrag(mTouchChild);
                }
            }
        });
    }

    public void setCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    private void beginDrag(View childView){
        if(childView == null){
            throw new NullPointerException();
        }
        checkCallback();
        //impl
        childView.setVisibility(View.INVISIBLE);
        mDispatchToAlertWindow = true;
        mItemManager.findDragItem(childView);
        childView.getLocationInWindow(mTempLocation);
        mWindomHelper.showView(mCallback.createWindowView(childView), mTempLocation[0],
                mTempLocation[1], true, mWindowCallback);
    }
    private boolean onMoveImpl(View view) {
        final List<Item> mItems = mItemManager.mItems;
        final Callback mCallback = this.mCallback;
        Item item = null;
        int centerX, centerY;
        boolean found = false;
        for(int i=0, size = mItems.size() ; i < size ; i++){
            item = mItems.get(i);
            item.view.getLocationOnScreen(mTempLocation);
            centerX = mTempLocation[0] + item.view.getWidth()/2;
            centerY = mTempLocation[1] + item.view.getHeight()/2;
            if(isViewUnderInScreen(view, centerX, centerY, false)  && item != mItemManager.mDragItem
                   && mCallback.isChildDraggable(item.view) ){
                infoWhenDebug("onMove_isViewUnderInScreen","index = " + item.index );
                /**
                 * Drag到target目标的center时，判断有没有已经hold item, 有的话，先删除旧的,
                 */
                found = true;
                break;
            }
        }
        if(found ){
            //the really index to add
            final int index = item.index;
            Item dragItem = mItemManager.mDragItem;
            // remove old
            removeView(mItemManager.mDragItem.view);
            //add hold
            View hold = mCallback.copyChildView(dragItem.view, dragItem.index);
            hold.setVisibility(View.INVISIBLE);  //隐藏
            addView(hold, index);
            //reset drag item and alert view
            mItemManager.findDragItem(hold);
            mCallback.setWindowViewByChild(mWindomHelper.getView(), mItemManager.mDragItem.view);
            infoWhenDebug("onMove","hold index = " + mItemManager.mDragItem.index);
        }
        return found;
    }

    public void releaseDrag() {
        releaseDragInternal();
        mIsDragState = false;
    }

    private void releaseDragInternal(){
        checkCallback();
        if(mItemManager.mDragItem!=null) {
            mItemManager.mDragItem.view.setVisibility(View.VISIBLE);
            mCallback.setChildByDragState(mItemManager.mDragItem.view, mIsDragState);
        }
        mWindomHelper.releaseView();
        mDispatchToAlertWindow = false;
        mTouchChild = null;
    }

    /**
     * @param dragState 是否拖拽状态，比如拖拽时该显示关闭按钮
     * @param showChildren 是否显示当前view的所有隐藏child(直接child，非递归)
     */
    public void setDragState(boolean dragState, boolean showChildren){
        checkCallback();
        this.mIsDragState = dragState;
        final Callback mCallback = this.mCallback;
        View view;
        for(int i=0, size = getChildCount(); i < size ;i++){
            view = getChildAt(i);
            if(showChildren && view.getVisibility() != View.VISIBLE){
                view.setVisibility(View.VISIBLE);
            }
            mCallback.setChildByDragState(view, dragState);
        }
    }

    private void checkCallback() {
        if(mCallback == null){
            throw new IllegalStateException("you must call #setCallback first.");
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
        checkCallback();
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
        checkCallback();
        mItemManager.onAddView(child, index, params) ;
        mCallback.setChildByDragState(child, mIsDragState);
    }

    @Override
    public void removeViewAt(int index) {
        super.removeViewAt(index);
        mItemManager.onRemoveViewAt(index);
        checkIfAutoReleaseDrag();
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        mItemManager.onRemoveView(view);
        checkIfAutoReleaseDrag();
    }

    @Override
    public void removeAllViews() {
        super.removeAllViews();
        mItemManager.onRemoveAllViews();
        checkIfAutoReleaseDrag();
    }

    private void checkIfAutoReleaseDrag() {
        if(getChildCount()==0){
            releaseDrag();
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //infoWhenDebug("onTouchEvent", event.toString());
        //infoWhenDebug("onTouchEvent", "------> mDispatchToAlertWindow = " + mDispatchToAlertWindow +" ,mIsDragState = " + mIsDragState);
        final boolean handled = mGestureDetector.onTouchEvent(event);
        //解决ScrollView嵌套DragFlowLayout时，引起的事件冲突
        if(getParent()!=null){
            getParent().requestDisallowInterceptTouchEvent(mDispatchToAlertWindow);
        }
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
