package study.heaven7.com.android_study.dialog;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.heaven7.core.util.Logger;

import java.lang.ref.WeakReference;

import study.heaven7.com.android_study.animate.BaseAnimator;
import study.heaven7.com.android_study.animate.ZoomInBottomEnter;
import study.heaven7.com.android_study.animate.ZoomOutBottomExit;

/**
 * have anim
 * Created by heaven7 on 2017/1/17.
 */

public class CommonDialog extends Dialog {

    private static final String TAG = "CommonDialog";
    private static final boolean DEBUG = false;

    private static final String KEY_POPUP_STYLE = "h7:CommonDialog:popup_style";
    private static final String KEY_AUTO_DISMISS = "h7:CommonDialog:auto_dismiss";
    private static final String KEY_ANIMATE_ROOT = "h7:CommonDialog:animate_root";

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private BaseAnimator mEnterAnim = new ZoomInBottomEnter();
    private BaseAnimator mExitAnim = new ZoomOutBottomExit();
    private WeakReference<View> mContentView;

    private boolean mIsPopupStyle;
    /**
     * is performing the animate
     */
    private boolean mPerformingAnim;
    private long mAutoDismissDelay;
    private boolean mAnimateOnRootView;

    private Callback mCallback;

    /**
     * the callback help we handle something.
     */
    public interface Callback {
        /**
         * called in {@link Dialog#onAttachedToWindow()} or the onAnimationEnd of enter animation.
         *
         * @param view the content view, not the view of android.R.id.content.
         */
        void afterShow(View view);

        /**
         * called before {@link Dialog#dismiss()} ()}.
         *
         * @param view the content view not the view of android.R.id.content.
         */
        void beforeDismiss(View view);
    }

    public CommonDialog(Context context) {
        super(context);
    }

    public CommonDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CommonDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (DEBUG) {
            Logger.i(TAG, "onAttachedToWindow", ""); //called every show
        }
        if (mEnterAnim != null) {
            mEnterAnim.listener(new InternalAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (!applyAutoDismiss()) {
                        dispatchAfterShow();
                    }
                }
            }).playOn(getAnimateView(), getWindow() != null
                    ? getWindow().getAttributes() : null);
        } else {
            dispatchAfterShow();
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (DEBUG) {
            Logger.i(TAG, "onDetachedFromWindow", "");
        }
    }

    @Override
    public void dismiss() {
        final View animateView = getAnimateView();
        if (DEBUG) {
            Logger.i(TAG, "dismiss", "animateView = " + animateView);
        }
        if (mCallback != null) {
            mCallback.beforeDismiss(getRealContentView());
        }
        if (mExitAnim == null || animateView == null) {
            dismissWithoutAnimation();
            return;
        }
        mExitAnim.listener(new InternalAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                dismissWithoutAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                dismissWithoutAnimation();
            }
        }).playOn(getAnimateView(), getWindow() != null
                ? getWindow().getAttributes() : null);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        findRealView();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        findRealView();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        findRealView();
    }

    @NonNull
    @Override
    public Bundle onSaveInstanceState() {
        final Bundle bundle = super.onSaveInstanceState();
        if (mIsPopupStyle) {
            bundle.putBoolean(KEY_POPUP_STYLE, true);
        }
        if (mAutoDismissDelay != 0) {
            bundle.putLong(KEY_AUTO_DISMISS, mAutoDismissDelay);
        }
        if (mAnimateOnRootView) {
            bundle.putBoolean(KEY_ANIMATE_ROOT, true);
        }
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        if(savedInstanceState.getBoolean(KEY_POPUP_STYLE)){
            mIsPopupStyle = true;
        }
        if(savedInstanceState.getBoolean(KEY_ANIMATE_ROOT)){
            mAnimateOnRootView = true;
        }
        mAutoDismissDelay = savedInstanceState.getLong(KEY_AUTO_DISMISS , 0);
        super.onRestoreInstanceState(savedInstanceState);
    }

    //==================================================================//

    private void findRealView() {
        if (getWindow() != null) {
            View view = getWindow().getDecorView();
            mContentView = new WeakReference<View>(((ViewGroup) view.findViewById(android.R.id.content)).getChildAt(0));
        }
    }

    private View getAnimateView() {
        View realView = mContentView != null ? mContentView.get() : null;
        if (mAnimateOnRootView || realView == null) {
            return getWindow() != null ? getWindow().getDecorView() : null;
        } else {
            return realView;
        }
    }

    private View getRealContentView() {
        return mContentView != null ? mContentView.get() : null;
    }

    private void dispatchAfterShow() {
        if (mCallback != null) {
            mCallback.afterShow(getRealContentView());
        }
    }

    /**
     * @return true if auto dismiss success.
     */
    private boolean applyAutoDismiss() {
        if (mAutoDismissDelay > 0) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismissWithoutAnimation();
                }
            }, mAutoDismissDelay);
            return true;
        }
        return false;
    }

    public void dismissWithoutAnimation() {
        super.dismiss();
        mContentView = null;
    }
    //==============================================================

    public CommonDialog autoDismiss(long delay) {
        this.mAutoDismissDelay = delay;
        return this;
    }

    public CommonDialog animateOnDecorView(boolean useDecorView) {
        this.mAnimateOnRootView = useDecorView;
        return this;
    }

    public CommonDialog withEnterAnimator(BaseAnimator animator) {
        this.mEnterAnim = animator;
        return this;
    }

    public CommonDialog withExitAnimator(BaseAnimator animator) {
        this.mExitAnim = animator;
        return this;
    }

    public CommonDialog dimEnabled(boolean isDimEnabled) {
        if (isDimEnabled) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        return this;
    }

    public CommonDialog callback(Callback callback) {
        this.mCallback = callback;
        return this;
    }

    public CommonDialog popupStyle(boolean enable) {
        mIsPopupStyle = enable;
        return this;
    }

    public void showAtLocation(int gravity, int x, int y) {
        if (mIsPopupStyle) {
            Window window = getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            window.setGravity(gravity);
            params.x = x;
            params.y = y;
        }
        show();
    }

    public void showAtLocation(int x, int y) {
        showAtLocation(Build.VERSION.SDK_INT >= 14 ? Gravity.START | Gravity.TOP
                : Gravity.LEFT | Gravity.TOP, x, y);
    }

    private class InternalAnimatorListener extends BaseAnimator.AnimatorListenerAdapter {
        @Override
        public void onAnimationStart(Animator animation) {
            mPerformingAnim = true;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mPerformingAnim = false;
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            mPerformingAnim = false;
        }
    }

}
