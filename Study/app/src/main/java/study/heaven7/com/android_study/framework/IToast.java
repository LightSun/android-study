package study.heaven7.com.android_study.framework;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.View;
import android.view.ViewGroup;


public interface IToast {

    byte TYPE_DEBUG = 1;
    byte TYPE_NORMAL = 2;
    byte TYPE_WARN = 3;
    byte TYPE_ERROR = 4;

    IToast type(byte type);

    IToast gravity(int gravity);

    IToast withStartAction(Runnable action);

    IToast withEndAction(Runnable action);

    IToast enableClick(boolean enable);

    IToast layout(@LayoutRes int layout, @Nullable ViewGroup parent, IViewBinder binder);

    IToast animateStyle(@StyleRes int animStyle);

    IToast position(int x, int y);

    void show(String msg);

    void show(int resId);

    boolean isShowing();

    void cancel();

    /**
     * the view binder
     */
    interface IViewBinder {
        /**
         * called on bind view
         * @param view the view of the toast layout.
         */
        void onBind(View view);
    }
}
