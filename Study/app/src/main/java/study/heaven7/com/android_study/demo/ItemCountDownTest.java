package study.heaven7.com.android_study.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.heaven7.adapter.BaseSelector;
import com.heaven7.adapter.QuickRecycleViewAdapter;
import com.heaven7.core.util.ViewHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.InjectView;
import study.heaven7.com.android_study.BaseActivity;
import study.heaven7.com.android_study.R;
import study.heaven7.com.android_study.item_count_down.CountDownManager;
import study.heaven7.com.android_study.item_count_down.CountDownTaskCallbackImpl;
import study.heaven7.com.android_study.item_count_down.ILeftTimeGetter;

/**
 * recycler view 或者list view里面item倒计时。测试
 * Created by heaven7 on 2016/9/9.
 */
public class ItemCountDownTest extends BaseActivity {

    private static final SimpleDateFormat DF = new SimpleDateFormat("HH:mm:ss");

    @InjectView(R.id.rv)
    RecyclerView mRv;

    QuickRecycleViewAdapter<TestBean> mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_item_count_down;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
         mRv.setLayoutManager(new LinearLayoutManager(this));
         mAdapter = new QuickRecycleViewAdapter<TestBean>(android.R.layout.simple_list_item_1,
                 new ArrayList<TestBean>()) {
             private CountDownManager<TestBean> mCDM;
             @Override
             protected void onFinalInit() {
                 mCDM = new CountDownManager<TestBean>(1000);
                 mCDM.attachCountDownTimer(this);
             }

             @Override
             protected void onBindData(Context context, int position, final TestBean item, int itemLayoutId, final ViewHelper helper) {
                 final TextView tv = helper.getView(android.R.id.text1);
                 mCDM.addCountDownCallback(item, new CountDownTaskCallbackImpl<TestBean>(position, tv) {
                     @Override
                     protected CharSequence format(int position, TestBean bean, long millisUntilFinished) {
                         return DF.format(new Date(millisUntilFinished) ) ;
                     }
                 });
             }
         };
        mRv.setAdapter(mAdapter);
        addTestData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void addTestData() {
        long minLeftTime = 20000; //最小20秒
        for(int i=0, size = 30 ;i<size ;i++){
            mAdapter.getAdapterManager().addItem(new TestBean(minLeftTime + i * 2000));//每个多2秒钟
        }
    }

    private static class TestBean extends BaseSelector implements ILeftTimeGetter{

        public long lefttime;

        public TestBean(long time) {
            this.lefttime = time;
        }
        @Override
        public long getLeftTime() {
            return lefttime;
        }
    }
}
