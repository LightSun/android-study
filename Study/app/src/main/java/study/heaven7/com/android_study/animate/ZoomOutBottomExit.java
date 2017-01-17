package study.heaven7.com.android_study.animate;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Created by heaven7 on 2017/1/17.
 */

public class ZoomOutBottomExit extends BaseAnimator {

    @Override
    public void setAnimation(AnimatorSet set, View view, int constraintWidth, int constraintHeight) {
       // int h = view.getMeasuredHeight();

        set.playTogether(//
                ObjectAnimator.ofFloat(view, "alpha", 1, 1, 0),//
                ObjectAnimator.ofFloat(view, "scaleX", 1, 0.475f, 0.1f),//
                ObjectAnimator.ofFloat(view, "scaleY", 1, 0.475f, 0.1f),//
                ObjectAnimator.ofFloat(view, "translationY", 0, -60, constraintHeight));
    }
}
