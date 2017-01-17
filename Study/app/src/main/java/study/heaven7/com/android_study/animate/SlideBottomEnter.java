package study.heaven7.com.android_study.animate;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by heaven7 on 2017/1/17.
 */

public class SlideBottomEnter extends BaseAnimator {

    @Override
    public void setAnimation(AnimatorSet set, View view, int constraintWidth, int constraintHeight) {
        DisplayMetrics dm = view.getContext().getResources().getDisplayMetrics();
        set.playTogether(//
                ObjectAnimator.ofFloat(view, "translationY", 250 * dm.density, 0), //
                ObjectAnimator.ofFloat(view, "alpha", 0.2f, 1));
    }
}
