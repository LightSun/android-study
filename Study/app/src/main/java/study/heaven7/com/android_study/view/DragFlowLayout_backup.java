package study.heaven7.com.android_study.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
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

import study.heaven7.com.android_study.alert_window.AlertWindowHelper;

/**
 * Created by heaven7 on 2016/8/1.
 */
public class DragFlowLayout_backup extends FlowLayout {

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

    private GestureDetectorCompat mMoveDetector;
    private int mTouchSlop;

    private ViewDragHelper mDragHelper;
    private boolean mDraggable;
    private ICallback mCallback;

    private View mDraggingView;
    private int mHolderIndex = INVALID_INDXE;
    private boolean mDragging;

    public interface ICallback{

        boolean isDraggable(DragFlowLayout_backup parent, View child);

        @NonNull View copyChildView(View v, int index);

        void onDragEnd(View releasedChild);
    }


    public DragFlowLayout_backup(Context context) {
        this(context,null);
    }

    public DragFlowLayout_backup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public DragFlowLayout_backup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    @TargetApi(21)
    public DragFlowLayout_backup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mWindomHelper = new AlertWindowHelper(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMoveDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx, float dy) {
                return  Math.abs(dy)> mTouchSlop ||  Math.abs(dx) > mTouchSlop;
            }
        });
        mDragHelper = ViewDragHelper.create(this, 1f, new DragHelperCallback());
    }

    public boolean isDraggable() {
        return mDraggable;
    }
    /** often called in onLongClick */
    public void setDraggable(boolean mDraggable) {
        this.mDraggable = mDraggable;
    }
    public void setDragCallback(ICallback mCallback) {
        this.mCallback = mCallback;
    }

 /*   @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 手指按下的时候，需要把某些view bringToFront，否则的话，tryCapture将不按预期工作
            View child = mDragHelper.findTopChildUnder((int) ev.getX(), (int) ev.getY());
            if(child!=null) {
                getParent().requestDisallowInterceptTouchEvent(true);
               // bringChildToFront(child);
            }else{
                getParent().requestDisallowInterceptTouchEvent(false);
            }
        }
        return super.dispatchTouchEvent(ev);
    }*/

    public void cancelDrag() {
        mDragHelper.abort();
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        logWhenDebug("onInterceptTouchEvent","mDraggable = "+ mDraggable +" ，event = "+ ev);
        if(mDraggable) {
            final boolean shouldIntercept = mDragHelper.shouldInterceptTouchEvent(ev);
            boolean moved = mMoveDetector.onTouchEvent(ev);
            logWhenDebug("onInterceptTouchEvent", "-----------> moved = " + moved +" ,mDragging = "+ mDragging
                    + " ,shouldIntercept = " + shouldIntercept + " <------------");
            if (!mDragging && moved) {
                View child = mDragHelper.findTopChildUnder((int) ev.getX(), (int) ev.getY());
                if (child != null) {
                    logWhenDebug("onInterceptTouchEvent", "begin captureChildView: " + child);
                    mDragging = true;
                    mDragHelper.captureChildView(child, 0);
                   // bringChildToFront(child); //添加这个后拖动的child会自动排序到末尾~~
                    return true;
                }
            }
           return shouldIntercept && moved;
        }else{
            /**
             * 很明显，上面captureChildView是在move中才会调用的，如果不让mDragHelper接收之前的down事件，会造成长按不离开时无法拖动.
             */
            if(ev.getAction()== MotionEvent.ACTION_DOWN){
                mDragHelper.shouldInterceptTouchEvent(ev);
                mDragHelper.processTouchEvent(ev);
            }
        }
        return super.onInterceptTouchEvent(ev);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        logWhenDebug("onTouchEvent","event = "+ event);
        try {
            // 该行代码可能会抛异常，正式发布时请将这行代码加上try catch
            mDragHelper.processTouchEvent(event);
        } catch (Exception ex) {
            // ex.printStackTrace();
        }
        return true;
    }


    private class DragHelperCallback extends ViewDragHelper.Callback {

        private final Rect mTempRect = new Rect();
        private final Rect mRawRect = new Rect();
        private boolean mfound ;
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            logWhenDebug("tryCaptureView", "mDraggable = " + mDraggable+", left = " + child.getLeft());
            if(!mDraggable){
                return false;
            }
            if(mCallback!=null && mCallback.isDraggable(DragFlowLayout_backup.this, child)){
                mDragging = true;
                mItemManager.findDragItemIndex(child);
                return true;
            }
            return false;
        }
        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            if(!mDraggable){
                return;
            }
            capturedChild.getHitRect(mRawRect);
            logWhenDebug("onViewCaptured", "===== capturedChild's Rect: "+ mRawRect);
           /* getHitRect(mTempRect);
            infoWhenDebug("onViewCaptured", "===== Parent's Rect: "+ mTempRect);*/
            mfound = false;
            mDraggingView = capturedChild;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if(mDraggingView != changedView){
                return;
            }
            logWhenDebug("onViewPositionChanged", "left = " + left +" ,top = "+ top);

           /* changedView.setDrawingCacheEnabled(true);
            final Bitmap bitmap = Bitmap.createBitmap(changedView.getDrawingCache());
            changedView.setDrawingCacheEnabled(false);
            moveProjectionTo(bitmap,left,top);*/

            changedView.getHitRect(mTempRect);
            logWhenDebug("onViewPositionChanged","==== changedView's Rect: " + mTempRect);

            //根据位置 计算对应的child and position
            //changedView范围包含 某个child的中心点重叠 就执行偏移动画. center, view
            final List<Item> items = mItemManager.mItems;
            if( items.size() == 0){
                return;
            }
            //有没已经默认添加的，如果有，先删除再添加,否则直接添加
            addHoldViewIfNeed(items);
            invalidate();
        }

        /**
         * 1, drag到target目标的center时，判断有没有已经holdIndex, 有的话，先删除旧的,
         * 2, 离开hold view中心点的时候也要处理：还原. (删除 hold item)
         * 3, 没有离开中心点的话，就删除拖拽的item, 将hold item转正.
         * @param items
         */
        private void addHoldViewIfNeed(List<Item> items) {
            int size = items.size();
            //find target item
            Item item = null;
            View v = null;
            int centerX;
            int centerY;

            boolean find = false;
            int holdIndex = DragFlowLayout_backup.this.mHolderIndex;

            for (int i = 0; i < size; i++) {
                item = items.get(i);
                v = item.view;
                centerX = (v.getLeft() + v.getRight()) / 2;
                centerY = (v.getTop() + v.getBottom()) / 2;
                //if:  拖动的view 和 某个item的view中心点重叠
               if(mDraggingView!=v && mDragHelper.isViewUnder(mDraggingView, centerX, centerY)){
                   //之前没有holder item或者 目标item不是 holder item
                   if(holdIndex == INVALID_INDXE || holdIndex != item.index) {
                       find = true;
                       break;
                   }
                }
            }
            mfound = find;
            if(find) {
                logWhenDebug("addHoldViewIfNeed", "mHolderIndex = " + mHolderIndex +" ,found index = "+ item.index);
                if(mHolderIndex != INVALID_INDXE){
                    removeViewAt(mHolderIndex);
                }
                v = mCallback.copyChildView(v, item.index);
                v.setVisibility(View.INVISIBLE); //视觉效果
                mHolderIndex = item.index;
                addView(v, item.index);
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            logWhenDebug("onViewReleased", "--------- end --------");
            if(mDraggingView!=null){
                if(mDraggingView != releasedChild){
                    Log.w(TAG, "mDraggingView != releasedChild");
                    return;
                }
                //删除dragItem如果需要
                if(mfound){
                    //removeViewAt(mItemManager.mMapDragItem.index);
                }else{
                   // removeViewAt(mHolderIndex);
                   // removeViewAt(mItemManager.mMapDragItem.index);
                   mDragHelper.smoothSlideViewTo(mDraggingView, mRawRect.left, mRawRect.top);
                }
                mDragging = false;
                mDraggingView = null;
                mHolderIndex = INVALID_INDXE;
                mItemManager.mDragItem = null;
                mCallback.onDragEnd(releasedChild);
            }
            invalidate();
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return left;
        }
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return top;
        }
    }

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

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    protected static void logWhenDebug(String method, String msg) {
        if(DEBUG) {
            Log.i(TAG, "called [ " + method + "() ]: " + msg);
        }
    }

    //已经添加的索引?
    //index, view, center?
    private static class Item{
        int index;
        View view;
    }

    //TODO
    private static class ItemManager {
        final List<Item> mItems = new ArrayList<>();
        Item mDragItem = null;

        public void onAddView(View child, int index, LayoutParams params) {
            index = index != -1 ? index : mItems.size();
            logWhenDebug("onAddView", "index = " + index );
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
        }

        public void onRemoveViewAt(int index) {
            logWhenDebug("onRemoveViewAt", "index = " + index );
            Item item;
            for(int i=0,size = mItems.size() ;i<size ;i++){
                item =  mItems.get(i);
                if(item.index > index){
                    item.index --;
                }
            }
            mItems.remove(index);
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
            logWhenDebug("onRemoveView", "targetIndex = " + targetIndex );
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
        }
        public void onRemoveAllViews() {
            mItems.clear();
        }
        public void findDragItemIndex(View capturedChild) {
            Item item;
            for(int i=0 ,size = mItems.size() ;i<size ;i++){
                item =  mItems.get(i);
                if(item.view == capturedChild){
                    mDragItem = item;
                    break;
                }
            }
        }
    }

}
