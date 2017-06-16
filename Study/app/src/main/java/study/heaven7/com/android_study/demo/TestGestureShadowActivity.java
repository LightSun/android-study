package study.heaven7.com.android_study.demo;

import android.os.Bundle;

import butterknife.InjectView;
import study.heaven7.com.android_study.BaseActivity;
import study.heaven7.com.android_study.R;
import study.heaven7.com.android_study.view.ShadowImageView;

/**
 * 手势跟随效果
 * Created by heaven7 on 2017/6/15 0015.
 */

public class TestGestureShadowActivity extends BaseActivity {


    @InjectView(R.id.shadow_iv)
    ShadowImageView shadow_mShadowView;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_gesture_shadow;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        shadow_mShadowView.setContentResource(R.drawable.common_ic_search);
    }
}
