package study.heaven7.com.android_study.demo;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import butterknife.InjectView;
import butterknife.OnClick;
import study.heaven7.com.android_study.BaseActivity;
import study.heaven7.com.android_study.R;

/**
 * https://github.com/wasabeef/recyclerview-animators
 * Created by heaven7 on 2017/4/6 0006.
 */

public class ItemAnimatorTest extends BaseActivity{

    @InjectView(R.id.vg_reveal)
    ViewGroup vg_reveal;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_item_animation;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    //测试 圆形缩放动画.api21+ . ViewAnimationUtils.createCircularReveal().
    @OnClick(R.id.bt_test_reveal)
    public void onClickTestReveal(View v){
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(vg_reveal, "scaleX", 0, 200);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(vg_reveal, "scaleY", 0, 200);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(500);
        set.setInterpolator(new LinearInterpolator());
        set.playTogether(scaleX, scaleY);
        set.start();
    }
}
