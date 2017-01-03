package study.heaven7.com.android_study.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.heaven7.adapter.BaseSelector;
import com.heaven7.adapter.QuickRecycleViewAdapter;
import com.heaven7.core.util.ViewHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import study.heaven7.com.android_study.BaseActivity;
import study.heaven7.com.android_study.R;

/**
 * Created by heaven7 on 2016/9/30.
 */
public class TestProgressBar extends BaseActivity {

    @InjectView(R.id.rv)
    RecyclerView mRv;

    @Override
    protected int getlayoutId() {
        return R.layout.ac_pregress_bar_in_itemview;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setAdapter(new QuickRecycleViewAdapter<BaseSelector>(R.layout.item_progress_bar,  createTestList()) {
            @Override
            protected void onBindData(Context context, int position, BaseSelector item, int itemLayoutId, ViewHelper helper) {

            }
        });
    }

    private List<BaseSelector> createTestList() {
        List<BaseSelector> list = new ArrayList<>();
        list.add(new BaseSelector());
        list.add(new BaseSelector());
        list.add(new BaseSelector());
        list.add(new BaseSelector());
        list.add(new BaseSelector());
        list.add(new BaseSelector());
        list.add(new BaseSelector());
        return list;
    }
}
