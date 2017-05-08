package study.heaven7.com.android_study.demo;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.widget.ListView;

import com.heaven7.adapter.BaseSelector;
import com.heaven7.adapter.QuickAdapter;
import com.heaven7.core.util.TextWatcherAdapter;
import com.heaven7.core.util.ViewHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import study.heaven7.com.android_study.BaseActivity;
import study.heaven7.com.android_study.R;
import study.heaven7.com.android_study.util.PinyinSearchUtil;
import study.heaven7.com.android_study.widget.ClearableEditText;

/**
 * Created by heaven7 on 2016/3/7.
 */
public class TestPinyinInSearchActivity extends BaseActivity implements
        PinyinSearchUtil.ISearchCallback<TestPinyinInSearchActivity.User> {

    @InjectView(R.id.et_search)
    ClearableEditText mEt;

    @InjectView(R.id.lv)
    ListView mLv;

    private List<User> mUserList;
    private List<User> mSearchList;
    private QuickAdapter<User> mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_pinyin_in_search;
    }

    @Override
    protected void initView() {
        mEt.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                //qwertySearch(s.toString());
                PinyinSearchUtil.qwertySearch(s.toString(),mUserList,TestPinyinInSearchActivity.this);
            }
        });
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        addTestData();
        mLv.setAdapter(mAdapter = new QuickAdapter<User>(android.R.layout.activity_list_item, mSearchList) {
            @Override
            protected void onBindData(Context context, int position, User item, int itemLayoutId, ViewHelper helper) {
                helper.setText(android.R.id.text1, item.getPinyinSearchHelper().getName());
            }
        });
    }

    private void addTestData() {
        List<User> list = new ArrayList<>();
        mUserList = list;
        list.add(new User("张诗楠"));    //zsn
        list.add(new User("快user快")); //ksk
        list.add(new User("很给力ma")); //hglm
        list.add(new User("嘎嘎"));     // gg
        list.add(new User("可以撒"));   //ky
        list.add(new User("马总"));     //mz
        list.add(new User("周鸿祎"));    //zhy
        list.add(new User("段天狼"));    //dtl
        list.add(new User("徐名签"));    //xmq
        list.add(new User("徐法签"));    //xfq
        list.add(new User("马法签"));    //mfq
        mSearchList = new ArrayList<>(mUserList);
    }

    @Override
    public void onEmptySearchContent(List<User> rawList) {
          mAdapter.getAdapterManager().replaceAllItems(rawList);
    }

    @Override
    public void onSearchResult(List<User> mSearchList) {
        mAdapter.getAdapterManager().replaceAllItems(mSearchList);
    }


    public static class User extends BaseSelector implements PinyinSearchUtil.IPinyinSearchBean{

        private final PinyinSearchUtil.PinyinSearchHelper mHelper;

        public User(String username) {
            mHelper = PinyinSearchUtil.create(username);
        }
        @Override
        public PinyinSearchUtil.PinyinSearchHelper getPinyinSearchHelper() {
            return mHelper;
        }
    }

}
