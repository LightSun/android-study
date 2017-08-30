package study.heaven7.com.android_study;

import java.util.List;

import study.heaven7.com.android_study.demo.AnyObjectArrayFromResTest;
import study.heaven7.com.android_study.demo.ItemAnimatorTest;
import study.heaven7.com.android_study.demo.SelfDrawableTest;
import study.heaven7.com.android_study.demo.StickyHeaderListViewDemo;
import study.heaven7.com.android_study.demo.TestCommonAdmireDialog;
import study.heaven7.com.android_study.demo.TestGestureMosaicActivity;
import study.heaven7.com.android_study.demo.TestGestureShadowActivity;
import study.heaven7.com.android_study.demo.TestSeekFuncActivity;
import study.heaven7.com.android_study.demo.TextureViewTest;
import study.heaven7.com.android_study.demo.TopLayoutRecyclerViewActivity;

/**
 * 所有demo的入口
 * Created by heaven7 on 2016/11/14.
 */
public class EnterActivity extends AbsMainActivity {

    @Override
    protected void addDemos(List<ActivityInfo> list) {
        list.add(new ActivityInfo(ApiDemoActivities.class, " Api demos")) ;
        list.add(new ActivityInfo(MainActivity.class, " old demos (see it in MainActivity)")) ;
        list.add(new ActivityInfo(TestGestureMosaicActivity.class, "MosaicView1 Test")) ;
        list.add(new ActivityInfo(TestCommonAdmireDialog.class, "CommonDialog Test")) ;
        list.add(new ActivityInfo(ItemAnimatorTest.class, "ItemAnimator Test")) ;
        list.add(new ActivityInfo(TopLayoutRecyclerViewActivity.class, "top layout recyclerview")) ;
        list.add(new ActivityInfo(TextureViewTest.class, "test TextureView")) ;
        list.add(new ActivityInfo(TestSeekFuncActivity.class, "test seek/shake")) ;
        list.add(new ActivityInfo(TestGestureShadowActivity.class, "test gesture shadow")) ;
        list.add(new ActivityInfo(AnyObjectArrayFromResTest.class, "test any object array from resource")) ;
        list.add(new ActivityInfo(SelfDrawableTest.class, "test self-drawable")) ;
        list.add(new ActivityInfo(StickyHeaderListViewDemo.class, "StickyHeaderListViewDemo")) ;
    }

}
