package study.heaven7.com.android_study;

import java.util.List;

import study.heaven7.com.android_study.demo.api.XfermodesTest;

/**
 * 所有demo的入口
 * Created by heaven7 on 2016/11/14.
 */
public class EnterActivity extends AbsMainActivity {

    @Override
    protected void addDemos(List<ActivityInfo> list) {
        list.add(new ActivityInfo(MainActivity.class, " old demos (see it in MainActivity)")) ;
        list.add(new ActivityInfo(XfermodesTest.class, "Xfermodes Test")) ;
    }

}
