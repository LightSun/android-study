package study.heaven7.com.android_study.demo;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.heaven7.core.util.ViewCompatUtil;

import butterknife.InjectView;
import butterknife.OnClick;
import study.heaven7.com.android_study.BaseActivity;
import study.heaven7.com.android_study.R;
import study.heaven7.com.android_study.drawable.IconDrawable;

/**
 * a demo show how to use self-drawable.
 * Created by heaven7 on 2017/7/18 0018.
 */

public class SelfDrawableTest extends BaseActivity {

    @InjectView(R.id.iv)
    ImageView mIv;

    @InjectView(R.id.vg)
    ViewGroup mVg;
    private IconDrawable mIconDrawable;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_self_drawable;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @OnClick(R.id.bt_src)
    public void onClickSrc(View v){
        Drawable drawable = getResources().getDrawable(R.drawable.ic_home_close);
        mIconDrawable = new IconDrawable(drawable, Color.RED);
        mIv.setImageDrawable(mIconDrawable);
    }

    @OnClick(R.id.bt_bg)
    public void onClickBg(View v){
        Drawable drawable = getResources().getDrawable(R.drawable.ic_home_close);
        mIconDrawable = new IconDrawable(drawable, Color.RED);
        //iconDrawable.setAlpha(122); //ok

        ViewCompatUtil.setBackgroundCompatible(mVg, mIconDrawable);
    }
    @OnClick(R.id.bt_param)
    public void onClickParam(View v){
        mIconDrawable.setDesiredWidthHeight(mIconDrawable.getDesiredIconWidth() + 10,
                mIconDrawable.getDesiredIconHeight() + 10);
    }
}
