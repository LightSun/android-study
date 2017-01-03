package study.heaven7.com.android_study.util;

import android.view.View;

/**
 * Created by heaven7 on 2016/8/5.
 */
public class ViewUtils {

    public static boolean isViewIntersect(int x, int y, View view) {
        if (x >= view.getLeft() && x < view.getRight() &&
                y >= view.getTop() && y < view.getBottom()) {
            return true;
        }
        return false;
    }
    public static boolean isViewUnderInScreen(View view, int x, int y) {
        if (view == null) {
            return false;
        }
        int [] mTempLocation = new int[2];
        view.getLocationOnScreen(mTempLocation);
        int w = view.getWidth();
        int h = view.getHeight();
        int viewX = mTempLocation[0];
        int viewY = mTempLocation[1];
        return x >= viewX && x < viewX + w
                && y >= viewY && y < viewY + h;
    }


}
