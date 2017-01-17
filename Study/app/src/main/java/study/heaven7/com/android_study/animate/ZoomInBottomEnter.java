package study.heaven7.com.android_study.animate;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

public class ZoomInBottomEnter extends BaseAnimator {

	@Override
	public void setAnimation(AnimatorSet set, View view, int constraintWidth, int constraintHeight) {
		//int h = view.getMeasuredHeight();

		set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", 0.1f, 0.475f, 1),
				ObjectAnimator.ofFloat(view, "scaleY", 0.1f, 0.475f, 1),
				ObjectAnimator.ofFloat(view, "translationY", constraintHeight, -60, 0),
				ObjectAnimator.ofFloat(view, "alpha", 0, 1, 1));
	}
}
