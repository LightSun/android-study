package study.heaven7.com.android_study.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.OverScroller;

import com.heaven7.core.util.ImageParser;
import com.heaven7.core.util.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by heaven7 on 2016/12/27.
 */
public class GestureHelper {

    private static final String TAG = "GestureHelper";
    private static final float ZOOM_MAX_AMOUNT = 2f;
    //fling 的 最大增量 x ,y
    private static final float MAX_Y_DELTA = 0.25f;
    private static final float MAX_X_DELTA = 0.25f;

    /**
     * 马赛克模式
     */
    public static final int MODE_MOSAIC = 1;
    /**
     * 擦除模式
     */
    public static final int MODE_ERASE = 2;

    private final View view;
    private final ScaleGestureDetector mScaleGestureDetector;
    private final GestureDetectorCompat mGestureDetector;
    private final OverScroller mScroller;
    private ImageParser mParser;
    /***
     * view content rect
     */
    private final Rect mContentRect = new Rect();
    /***
     * 图片要绘制的范围 (as mCurrentViewport )
     */
    private final Rect mImageRect = new Rect();

    private final Point mZoomFocalPoint = new Point();

    private final Paint mMosaicPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    //原始图片宽度
    private int mImageWidth;
    //原始图片高度
    private int mImageHeight;

    private Bitmap mBaseLayer;
    private Bitmap mCoverLayer;
    private Bitmap mMosaicLayer;

    private int mMosaicColor = 0xFF4D4D4D;

    private int mMode = MODE_MOSAIC;

    private final List<Path> mMosaicPaths;
    private final List<Path> mErasePaths;
    private final List<GroupPointInfo> mMosaicPoints;
    private GroupPointInfo mCurrentInfo;
    private Path mCurrPath;
    private Path mTouchPath;
    private final Path mTempPath = new Path();

    private float mPathWidth = 20f;

    private long mFirstDownTime;
    private int mScrollPointerId;
    private boolean mDoubleFinger;

    public GestureHelper(View view) {
        final Context context = view.getContext();
        this.view = view;
        this.mScroller = new OverScroller(context);
        this.mGestureDetector = new GestureDetectorCompat(context, new GestureListenerImpl());
        this.mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureListenerImpl());

        mMosaicPaths = new ArrayList<>();
        mErasePaths = new ArrayList<>();
        mMosaicPoints = new ArrayList<>();
    }

    public void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        mContentRect.set(
                view.getPaddingLeft(),
                view.getPaddingTop(),
                view.getWidth() - view.getPaddingRight(),
                view.getHeight() - view.getPaddingBottom());
        mImageRect.set(mContentRect);
        if (mParser == null) {
            mParser = new ImageParser(mContentRect.width(), mContentRect.height());
        }
    }

    public void setSrcPath(String absPath) {
        File file = new File(absPath);
        if (!file.exists()) {
            Log.w(TAG, "invalid file path " + absPath);
            return;
        }
        reset();

        final Bitmap bitmap = mParser.parseToBitmap(absPath);
        this.mBaseLayer = bitmap;

        mImageWidth = bitmap.getWidth();
        mImageHeight = bitmap.getHeight();

        String fileName = file.getName();
        String parent = file.getParent();
        int index = fileName.lastIndexOf(".");
        String stem = fileName.substring(0, index);
        String newStem = stem + "_mosaic";
        fileName = fileName.replace(stem, newStem);
        String outPath = parent + "/" + fileName;

        constrainImageRect(mContentRect, mImageWidth, mImageHeight);


        updateColorMosaicCover();
        mMosaicLayer = null;

        ViewCompat.postInvalidateOnAnimation(view);
    }

    public void onDraw(Canvas canvas) {
        if (mBaseLayer != null) {
            canvas.drawBitmap(mBaseLayer, null, mImageRect, null);
        }
        if (mMosaicLayer != null) {
            canvas.drawBitmap(mMosaicLayer, null, mImageRect, null);
        }
        canvas.save();
        drawPath(canvas);
        canvas.restore();
    }

    private void drawPath(Canvas canvas) {
        // canvas.translate(mImageRect.left, mImageRect.top);
        Paint paint = this.mMosaicPaint;
        paint.reset();
        //   Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setPathEffect(new CornerPathEffect(10));
        paint.setStrokeWidth(mPathWidth);
        paint.setColor(Color.BLUE);

        if (mCurrPath != null) {
            canvas.drawPath(mCurrPath, paint);
        } else {
            final int imageLeft = mImageRect.left;
            final int imageTop = mImageRect.top;
            final int imageBottom = mImageRect.bottom;
            for (GroupPointInfo gpi : mMosaicPoints) {
                mTempPath.reset();
                for (PointInfo info : gpi.getPointInfos()) {
                    float x = info.getX();
                    float y = info.getY();
                    if (info.isStartPoint()) {
                        mTempPath.moveTo(x + imageLeft, imageBottom - y);
                    } else {
                        mTempPath.lineTo(x + imageLeft, imageBottom - y);
                    }
                }
                canvas.drawPath(mTempPath, paint);
            }
        }

        if (mTouchPath != null) {
            paint.setColor(Color.BLACK);
            canvas.drawPath(mTouchPath, paint);
        }

        paint.reset();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mImageRect.centerX(), mImageRect.centerY(), 5, paint);
        Logger.i("GestureHelper", "drawPath", "relative to whole content : image   -> w = " + mImageRect.width()
                + " , h = " + mImageRect.height());
        Logger.i("GestureHelper", "drawPath", "relative to whole content : content -> w = " + mContentRect.width()
                + " , h = " + mContentRect.height());
    }

    private void updateMosaics() {
        updateColorMosaicCover();
        updatePathMosaic();
    }

    private void updatePathMosaic() {
        final float scale = mImageRect.height() * 1f / mImageHeight;
        final int mImageWidth = mImageRect.width();
        final int mImageHeight = mImageRect.height();
        Logger.i("GestureHelper", "updatePathMosaic", "scale = " + scale + " ,w = "
                + mImageWidth + " ,h = " + mImageHeight);

        long time = System.currentTimeMillis();
        if (mMosaicLayer != null) {
            mMosaicLayer.recycle();
            mMosaicLayer = null;
        }
        mMosaicLayer = Bitmap.createBitmap(mImageWidth, mImageHeight,
                Bitmap.Config.ARGB_8888);

        Bitmap bmTouchLayer = Bitmap.createBitmap(mImageWidth, mImageHeight,
                Bitmap.Config.ARGB_8888);

        Paint paint = this.mMosaicPaint;
        paint.reset();
        //   Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setPathEffect(new CornerPathEffect(10));
        //TODO
        paint.setStrokeWidth(mPathWidth * scale);
        paint.setColor(Color.BLUE);

        Canvas canvas = new Canvas(bmTouchLayer);

        for (Path path : mMosaicPaths) {
            canvas.drawPath(path, paint);
        }
        paint.setColor(Color.TRANSPARENT);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        for (Path path : mErasePaths) {
            canvas.drawPath(path, paint);
        }
        //绘制马赛克
        canvas.setBitmap(mMosaicLayer);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawBitmap(mCoverLayer, 0, 0, null); //cover 覆盖到马赛克

        paint.reset();
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(bmTouchLayer, 0, 0, paint);
        paint.setXfermode(null);
        canvas.save();

        bmTouchLayer.recycle();
        Log.d(TAG, "updatePathMosaic " + (System.currentTimeMillis() - time));
    }

    private void updateColorMosaicCover() {
        final int mImageWidth = mImageRect.width();
        final int mImageHeight = mImageRect.height();
        Bitmap bitmap = Bitmap.createBitmap(mImageWidth, mImageHeight,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Rect rect = new Rect(0, 0, mImageWidth, mImageHeight);
        Paint paint = new Paint();
        paint.setColor(mMosaicColor);
        canvas.drawRect(rect, paint);
        canvas.save();
        mCoverLayer = bitmap;
    }

    public void reset() {
        if (mCoverLayer != null) {
            mCoverLayer.recycle();
            mCoverLayer = null;
        }
        if (mBaseLayer != null) {
            mBaseLayer.recycle();
            mBaseLayer = null;
        }
        if (mMosaicLayer != null) {
            mMosaicLayer.recycle();
            mMosaicLayer = null;
        }
        mMosaicPaths.clear();
        mErasePaths.clear();
    }

    /**
     * 约束图片真实的范围rect
     */
    private void constrainImageRect(Rect expect, int imageW, int imageH) {
        //居中imageRect
        final int viewWidth = mContentRect.width();
        final int viewHeight = mContentRect.height();
       /* final int imageW = mImageRect.width();
        final int imageH = mImageRect.height();

        int left = mImageRect.left;
        int right = mImageRect.right;
        int top = mImageRect.top;
        int bottom = mImageRect.bottom;*/
        int left = expect.left;
        int right = expect.right;
        int top = expect.top;
        int bottom = expect.bottom;

        if (imageW < viewWidth) {
            //调整位置，水平居中
            int halfDeltaW = (viewWidth - imageW) / 2;
            left = view.getPaddingLeft() + halfDeltaW;
            right = left + imageW;
        }
        if (imageH < viewHeight) {
            //调整位置，竖直居中
            int halfDeltaH = (viewHeight - imageH) / 2;
            top = view.getPaddingTop() + halfDeltaH;
            bottom = top + imageH;
        }
        mImageRect.set(left, top, right, bottom);
    }

    private boolean hitTest(float x, float y, Point dest) {
        if (!mImageRect.contains((int) x, (int) y)) {
            return false;
        }
        dest.set((int) (mImageRect.left + mImageRect.width()
                        * (x - mContentRect.left) / mContentRect.width()),
                (int) (mImageRect.top + mImageRect.height()
                        * (y - mContentRect.bottom) / -mContentRect.height()));
        return true;
    }

    private void fling(int velocityX, int velocityY) {
        Logger.i("GestureHelper", "fling", "velocityX = " + velocityX + " ,velocityY = " + velocityY);
        // Flings use math in pixels (as opposed to math based on the viewport).
        mScroller.forceFinished(true);
        mScroller.fling(
                mImageRect.left,
                mImageRect.top,
                velocityX,
                velocityY,
                0, (int) (mImageRect.width() * MAX_X_DELTA),
                0, (int) (mImageRect.height() * MAX_Y_DELTA));
        //  mContentRect.width() / 2,
        //  mContentRect.height() / 2);
        ViewCompat.postInvalidateOnAnimation(view);
    }

    public void computeScroll() {
        //TODO
        boolean needsInvalidate = false;
        if (mScroller.computeScrollOffset()) {
            final int currX = mScroller.getCurrX();
            final int currY = mScroller.getCurrY();
            final int w = mImageRect.width();
            final int h = mImageRect.height();
            mImageRect.set(currX, currY, currX + w, currY + h);
            needsInvalidate = true;
        }
        if (needsInvalidate) {
            ViewCompat.postInvalidateOnAnimation(view);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);
        final int actionIndex = MotionEventCompat.getActionIndex(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mScrollPointerId = event.getPointerId(0);
                mFirstDownTime = System.currentTimeMillis();
                mDoubleFinger = false;
            }
            break;

            case MotionEventCompat.ACTION_POINTER_DOWN: {
                mScrollPointerId = event.getPointerId(actionIndex);
                if ((System.currentTimeMillis() - mFirstDownTime) < 250) {
                    mDoubleFinger = true;
                }
            }
            break;
        }
        handleMosaicEvent(event, action);
        boolean result = mScaleGestureDetector.onTouchEvent(event);
        result |= mGestureDetector.onTouchEvent(event);
        return result;
    }

    private void handleMosaicEvent(MotionEvent event, int action) {
        if (event.getPointerCount() >= 2) {
            return;
        }
        //单指马赛克
        //TODO
        if (!hitTest(event.getX(), event.getY(), mZoomFocalPoint)) {
            return;
        }
        calculatePoint(mZoomFocalPoint);
        //记录相对于image rect的坐标。这样就不会出问题.不过注意缩放.
        calculateRelativeCoordinate(mZoomFocalPoint, mZoomFocalPoint);

        Logger.i("GestureHelper", "handleMosaicEvent", "hitTest ok. point = " + mZoomFocalPoint);
        if (action == MotionEvent.ACTION_DOWN) {
            mCurrentInfo = new GroupPointInfo();
            mMosaicPoints.add(mCurrentInfo);
            mCurrentInfo.addPointInfo(new PointInfo(mZoomFocalPoint, true));
            // mCurrPath = new Path();
            // mCurrPath.moveTo(mZoomFocalPoint.x, mZoomFocalPoint.y);
            mTouchPath = new Path();
            mTouchPath.moveTo(event.getX(), event.getY());
            /*switch (mMode) {
                case MODE_MOSAIC:
                    mMosaicPaths.add(mCurrPath);
                    break;

                case MODE_ERASE:
                    mErasePaths.add(mCurrPath);
                    break;

                default:
                    Logger.w("GestureHelper", "handleMosaicEvent", "unknown mode = " + mMode);
            }*/
        } else if (action == MotionEvent.ACTION_MOVE) {
            mCurrentInfo.addPointInfo(new PointInfo(mZoomFocalPoint, false));
            // mCurrPath.lineTo(mZoomFocalPoint.x, mZoomFocalPoint.y);
            mTouchPath.lineTo(event.getX(), event.getY());
            //TODO updateMosaics();
            ViewCompat.postInvalidateOnAnimation(view);
        }
    }

    private void updateMosaicPoints(float scale) {
        for (GroupPointInfo info : mMosaicPoints) {
            for (PointInfo pi : info.getPointInfos()) {
                pi.scale(scale);
            }
        }
        //缩放画笔
        mPathWidth *= scale;
    }

    //计算相对于image rect的坐标 (相对左下角)
    private void calculateRelativeCoordinate(Point in, Point out) {
        out.x = in.x - mImageRect.left;
        out.y = mImageRect.bottom - in.y;
    }

    //mZoomFocalPoint in and out
    private void calculatePoint(Point mZoomFocalPoint) {
        int imageCenterX = mContentRect.centerX();
        int imageCenterY = mContentRect.centerY();
        if (mZoomFocalPoint.x < imageCenterX) {
            mZoomFocalPoint.x = imageCenterX - (imageCenterX - mZoomFocalPoint.x) * mContentRect.width() / mImageRect.width();
        } else if (mZoomFocalPoint.x > imageCenterX) {
            mZoomFocalPoint.x = imageCenterX + (mZoomFocalPoint.x - imageCenterX) * mContentRect.width() / mImageRect.width();
        }
        if (mZoomFocalPoint.y > imageCenterY) {
            mZoomFocalPoint.y = imageCenterY - (mZoomFocalPoint.y - imageCenterY) * mContentRect.height() / mImageRect.height();
        } else if (mZoomFocalPoint.y < imageCenterY) {
            mZoomFocalPoint.y = imageCenterY + (imageCenterY - mZoomFocalPoint.y) * mContentRect.height() / mImageRect.height();
        }
    }

    /**
     * The scale listener, used for handling multi-finger scale gestures.
     */
    private class ScaleGestureListenerImpl implements ScaleGestureDetector.OnScaleGestureListener {
        /**
         * This is the active focal point in terms of the viewport. Could be a local
         * variable but kept here to minimize per-frame allocations.
         */
        private Point viewportFocus = new Point();
        private float lastSpanX;
        private float lastSpanY;

        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            lastSpanX = ScaleGestureDetectorUtil.getCurrentSpanX(scaleGestureDetector);
            lastSpanY = ScaleGestureDetectorUtil.getCurrentSpanY(scaleGestureDetector);
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            //TODO 是否要约束最小和最大
        }

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float spanX = ScaleGestureDetectorUtil.getCurrentSpanX(scaleGestureDetector);
            float spanY = ScaleGestureDetectorUtil.getCurrentSpanY(scaleGestureDetector);

            // 计算本次和上次相比的scale
           /* float newWidth = lastSpanX / spanX * mImageRect.width();
            float newHeight = lastSpanY / spanY * mImageRect.height();*/
            float scale = scaleGestureDetector.getScaleFactor();
            int newWidth = (int) (scale * mImageRect.width());
            int newHeight = (int) (scale * mImageRect.height());
         /*   if (newWidth > mImageWidth * ZOOM_MAX_AMOUNT ) {
                newWidth = mImageWidth * ZOOM_MAX_AMOUNT;
                scale = newWidth * 1f / mImageRect.width();
            }
            if(newHeight > mImageHeight * ZOOM_MAX_AMOUNT){
                newHeight = mImageHeight * ZOOM_MAX_AMOUNT;
                scale = newHeight * 1f / mImageRect.height();
            }*/
            updateMosaicPoints(newWidth * 1f / mImageRect.width());
            //TODO record scale

            float focusX = scaleGestureDetector.getFocusX();
            float focusY = scaleGestureDetector.getFocusY();
            hitTest(focusX, focusY, viewportFocus);

            mImageRect.set(
                    (int) (viewportFocus.x - newWidth * (focusX - mContentRect.left) / mContentRect.width()),
                    (int) (viewportFocus.y - newHeight * (mContentRect.bottom - focusY) / mContentRect.height()),
                    0,
                    0);
            mImageRect.right = mImageRect.left + newWidth;
            mImageRect.bottom = mImageRect.top + newHeight;
            constrainImageRect(mImageRect, mImageRect.width(), mImageRect.height());
            ViewCompat.postInvalidateOnAnimation(view);

            lastSpanX = spanX;
            lastSpanY = spanY;
            return true;
        }
    }

    /**
     * The gesture listener, used for handling simple gestures such as double touches, scrolls,
     * and flings.
     */
    //TODO GestureDetector 对于双指探测移动有问题。
    private class GestureListenerImpl extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            //  releaseEdgeEffects();
            mScroller.forceFinished(true);
            ViewCompat.postInvalidateOnAnimation(view);
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (hitTest(e.getX(), e.getY(), mZoomFocalPoint)) {
                scaleImageRect(e.getX(), e.getY(), mZoomFocalPoint);
                // ObjectAnimator.ofInt(GestureHelper.this,)
            }
            ViewCompat.postInvalidateOnAnimation(view);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //双指移动
            if (!mDoubleFinger) {
                return false;
            }
            // Scrolling uses math based on the viewport (as opposed to math using pixels).
            int viewportOffsetX = (int) (distanceX * mImageRect.width() / mContentRect.width());
            int viewportOffsetY = (int) (-distanceY * mImageRect.height() / mContentRect.height());
            //TODO need test
            setViewportBottomLeft(mImageRect.left - viewportOffsetX, mImageRect.bottom + viewportOffsetY);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //fling((int) velocityX, (int) velocityY);
            return true;
        }
    }

    private void scaleImageRect(float focusX, float focusY, Point viewportFocus) {
        final int width = mImageRect.width();
        final int height = mImageRect.height();
        final int newWidth;
        final int newHeight;
        if (width >= mImageWidth * ZOOM_MAX_AMOUNT) {
            newWidth = mImageWidth;
        } else {
            newWidth = (int) (mImageWidth * ZOOM_MAX_AMOUNT);
        }
        if (height >= mImageHeight * ZOOM_MAX_AMOUNT) {
            newHeight = mImageHeight;
        } else {
            newHeight = (int) (mImageHeight * ZOOM_MAX_AMOUNT);
        }
        updateMosaicPoints(newWidth * 1f / width);
        mImageRect.set(
                (int) (viewportFocus.x - newWidth * (focusX - mContentRect.left) / mContentRect.width()),
                (int) (viewportFocus.y - newHeight * (mContentRect.bottom - focusY) / mContentRect.height()),
                0,
                0);
        mImageRect.right = mImageRect.left + newWidth;
        mImageRect.bottom = mImageRect.top + newHeight;
        constrainImageRect(mImageRect, newWidth, newHeight);
        ViewCompat.postInvalidateOnAnimation(view);
    }

    private void setViewportBottomLeft(int x, int y) {
        final int currWidth = mImageRect.width();
        final int currHeight = mImageRect.height();
        mImageRect.set(x, y - currHeight, x + currWidth, y);
        ViewCompat.postInvalidateOnAnimation(view);
    }

    /*private static final Property<GestureHelper,Integer> sStartProp = new Property<GestureHelper, Integer>(
            Integer.class) {
        @Override
        public Integer get(GestureHelper object) {

            return null;
        }
    };*/

}
