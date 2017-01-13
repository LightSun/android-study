package study.heaven7.com.android_study;

import java.util.List;

import study.heaven7.com.android_study.demo.api.SortedListActivity;
import study.heaven7.com.android_study.demo.api.XfermodesTest;

/**
 * Created by heaven7 on 2017/1/13.
 */

public class ApiDemoActivities extends AbsMainActivity {
    @Override
    protected void addDemos(List<ActivityInfo> list) {
        list.add(new ActivityInfo(XfermodesTest.class, "Xfermodes Test")) ;
        list.add(new ActivityInfo(SortedListActivity.class, "SortedList Test")) ;
    }
}
