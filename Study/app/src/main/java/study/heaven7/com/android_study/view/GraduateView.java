package study.heaven7.com.android_study.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import study.heaven7.com.android_study.R;

/**
 * 刻度 view
 * Created by heaven7 on 2016/12/30.
 */
public class GraduateView extends View {

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float mStrokeVerWith = 5;
    private float mStrokeHorWith = 5;
    private int mGrduateVerColor = Color.RED;
    private int mGrduateHorColor = Color.RED;
    /**
     * 刻度个数,必须 >= 2
     */
    private int mGraduateCount = 5;
    private int mGrduateVerHeight = 10;
    /**
     * 绘制竖直线：是否从水平线底部开始绘制
     */
    private boolean mDrawVerticalInBottom = false;

    public GraduateView(Context context) {
        super(context);
        init(context, null);
    }

    public GraduateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GraduateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPaint.setStyle(Paint.Style.STROKE);

        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GraduateView);
            try {
                mStrokeVerWith = a.getFloat(R.styleable.GraduateView_gv_StrokeVerWith, mStrokeVerWith);
                mStrokeHorWith = a.getFloat(R.styleable.GraduateView_gv_StrokeHorWith, mStrokeHorWith);
                mGrduateVerColor = a.getColor(R.styleable.GraduateView_gv_GrduateVerColor, mGrduateVerColor);
                mGrduateHorColor = a.getColor(R.styleable.GraduateView_gv_GrduateHorColor, mGrduateHorColor);
                mGraduateCount = a.getInt(R.styleable.GraduateView_gv_GraduateCount, mGraduateCount);
                mGrduateVerHeight = a.getInt(R.styleable.GraduateView_gv_GrduateVerHeight, mGrduateVerHeight);
                mDrawVerticalInBottom = a.getBoolean(R.styleable.GraduateView_gv_DrawVerticalInBottom, mDrawVerticalInBottom);
            } finally {
                a.recycle();
            }
        }
    }

    public void setDrawVerticalInBottom(boolean drawVerticalInBottom) {
        if (mDrawVerticalInBottom != drawVerticalInBottom) {
            this.mDrawVerticalInBottom = drawVerticalInBottom;
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setStrokeVerticalWith(float width) {
        if (mStrokeVerWith != width) {
            this.mStrokeVerWith = width;
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setmStrokeHorizontalWith(float width) {
        if (mStrokeHorWith != width) {
            this.mStrokeHorWith = width;
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setGrduateHorizontalColor(int color) {
        if (this.mGrduateHorColor != color) {
            this.mGrduateHorColor = color;
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setmGrduateVerticalColor(int color) {
        if (this.mGrduateVerColor != color) {
            this.mGrduateVerColor = color;
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setGraduateCount(int count) {
        if (count < 2) {
            throw new IllegalArgumentException();
        }
        if (this.mGraduateCount != count) {
            this.mGraduateCount = count;
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setmGrduateVerticalHeight(int height) {
        if (this.mGrduateVerHeight != height) {
            this.mGrduateVerHeight = height;
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制刻度背景
        //以padding draw 俩边外观刻度
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int centerY = getHeight() / 2;
        final int rightPort = getWidth() - paddingRight;

        mPaint.setStrokeWidth(mStrokeHorWith);
        mPaint.setColor(mGrduateHorColor);
        canvas.drawLine(0, centerY, getWidth(), centerY, mPaint);

        //绘制竖直线
        mPaint.setStrokeWidth(mStrokeVerWith);
        mPaint.setColor(mGrduateVerColor);
        final int adjustY = (int) (mDrawVerticalInBottom ? (centerY + mStrokeHorWith / 2) : (centerY - mStrokeHorWith / 2));
        canvas.drawLine(paddingLeft, adjustY, paddingLeft, adjustY - mGrduateVerHeight, mPaint);
        canvas.drawLine(rightPort, adjustY, rightPort, adjustY - mGrduateVerHeight, mPaint);

        final int count = mGraduateCount - 2 + 1;
        final int blockSize = (rightPort - paddingLeft) / count;
        int left;
        for (int i = 0; i < count; i++) {
            left = paddingLeft + blockSize * (i + 1);
            canvas.drawLine(left, adjustY, left, adjustY - mGrduateVerHeight, mPaint);
        }
    }
}
