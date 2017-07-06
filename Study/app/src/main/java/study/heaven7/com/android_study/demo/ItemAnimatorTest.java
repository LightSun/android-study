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
import study.heaven7.com.android_study.temp.AutoZoomInImageView;
import study.heaven7.com.android_study.util.DimenUtil;

/**
 * https://github.com/wasabeef/recyclerview-animators
 * Created by heaven7 on 2017/4/6 0006.
 */

public class ItemAnimatorTest extends BaseActivity{

    @InjectView(R.id.vg_reveal)
    ViewGroup vg_reveal;
    @InjectView(R.id.vg_parent)
    ViewGroup vg_parent;

    @InjectView(R.id.aziv)
    AutoZoomInImageView mAziv;

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

    @OnClick(R.id.bt_escale)
    public void onClickEscale(View v){
        // 图片缩放动画，等比例
        float scaleH = (vg_parent.getHeight() - mAziv.getHeight()) * 1f / mAziv.getHeight();
        float scaleW = (vg_parent.getWidth() - mAziv.getWidth()) * 1f / mAziv.getWidth();

        final float scale = scaleH > scaleW ? scaleH : scaleW;
        mAziv.post(new Runnable() {//iv即AutoZoomInImageView
            @Override
            public void run() {
                //简单方式启动放大动画
//                iv.init()
//                  .startZoomInByScaleDeltaAndDuration(0.3f, 1000, 1000);//放大增量是0.3，放大时间是1000毫秒，放大开始时间是1000毫秒以后
                //使用较为具体的方式启动放大动画
                mAziv.init()
                        .setScaleDelta(scale)//放大的系数是原来的（1 + 0.2）倍
                        .setDurationMillis(1500)//动画的执行时间为1500毫秒
                        .setOnZoomListener(new AutoZoomInImageView.OnZoomListener(){
                            @Override
                            public void onStart(View view) {
                                //放大动画开始时的回调
                            }
                            @Override
                            public void onUpdate(View view, float progress) {
                                //放大动画进行过程中的回调 progress取值范围是[0,1]
                            }
                            @Override
                            public void onEnd(View view) {
                                //放大动画结束时的回调
                            }
                        })
                        .start(1000);//延迟1000毫秒启动
            }
        });
    }

}
