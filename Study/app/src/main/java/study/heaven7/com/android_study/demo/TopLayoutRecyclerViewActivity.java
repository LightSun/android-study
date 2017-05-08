package study.heaven7.com.android_study.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.heaven7.adapter.BaseSelector;
import com.heaven7.adapter.QuickRecycleViewAdapter;
import com.heaven7.core.util.ViewHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import study.heaven7.com.android_study.BaseActivity;
import study.heaven7.com.android_study.R;
import study.heaven7.com.android_study.help.CenterLayoutManager;

/**
 * Created by heaven7 on 2017/4/14 0014.
 */

public class TopLayoutRecyclerViewActivity extends BaseActivity {

    @InjectView(R.id.rv)
    RecyclerView mRv;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_item_count_down;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mRv.setLayoutManager(new CenterLayoutManager(this));

        mRv.setAdapter(new QuickRecycleViewAdapter<Bean>(R.layout.item_top_layout_m, getTestList()) {
            @Override
            protected void onBindData(Context context, final int position, Bean item, int itemLayoutId, ViewHelper helper) {
                 helper.setVisibility(R.id. tv_text2,item.isSelected())
                         .setText(R.id.tv_text1, item.text1)
                         .setText(R.id.tv_text2, item.text2)
                         .setRootOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View v) {
                                 getSelectHelper().select(position);
                                 mRv.smoothScrollToPosition(position);
                             }
                         });
            }
        });
    }

    private List<Bean> getTestList() {
        List<Bean> list = new ArrayList<>();
        for(int i = 0 ; i < 50 ;i ++){
            list.add(new Bean("TopLayoutRecyclerViewActivity_" + i));
        }
        list.get(0).setSelected(true);
        return list;
    }

    private static class Bean extends BaseSelector{
        final String text1;
        final String text2;

        public Bean(String text) {
            this.text1 = text+"______1";
            this.text2 = text+"______2";
        }
    }

}
