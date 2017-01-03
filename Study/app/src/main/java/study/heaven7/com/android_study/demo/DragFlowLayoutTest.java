package study.heaven7.com.android_study.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;

import butterknife.InjectView;
import study.heaven7.com.android_study.BaseActivity;
import study.heaven7.com.android_study.R;
import study.heaven7.com.android_study.fragment.LayoutAnimatorFragment;

/**
 * Created by heaven7 on 2016/8/1.
 */
public class DragFlowLayoutTest extends BaseActivity {

    @InjectView(R.id.fl_container)
    FrameLayout mFl_container;

    @InjectView(R.id.gridLayout)
    GridLayout mGridLayout;


    @Override
    protected int getlayoutId() {
        return R.layout.ac_drag_flow;
    }

    @Override
    protected void initView() {
        mFl_container.setVisibility(View.VISIBLE);
        replaceFragment(R.id.fl_container, new LayoutAnimatorFragment(),false);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }
}
