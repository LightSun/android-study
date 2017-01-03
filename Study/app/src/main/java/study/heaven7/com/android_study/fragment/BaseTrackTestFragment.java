package study.heaven7.com.android_study.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.heaven7.core.util.BundleHelper;
import com.heaven7.core.util.Logger;

import study.heaven7.com.android_study.R;
import study.heaven7.com.android_study.demo.ShowTextActivity;
import study.heaven7.com.android_study.help.TagTracker;
import study.heaven7.com.android_study.help.TrackTestUtil;

/**
 * Created by heaven7 on 2016/4/28.
 */
public  abstract class BaseTrackTestFragment extends BaseFragment{

    @Override
    protected int getlayoutId() {
        return R.layout.frag_track_test;
    }

    @Override
    protected void initView(Context context) {
         getViewHelper().setOnClickListener(R.id.bt, new View.OnClickListener() {
             public void onClick(View v) {
                 onClickBt(v);
             }
         }).setText(R.id.bt, getButtonName())
         .setText(R.id.bt_tag , getSmallTagName())
         .setOnClickListener(R.id.bt_tag, new View.OnClickListener() {
             public void onClick(View v) {
                 onClickTag(v);
             }
         });
    }

    @Override
    protected void initData(Context context, Bundle savedInstanceState) {

    }

    /**
     * 进入下一个界面后自动track and untrack.
     */
    protected void launchShowTextActivity(String text){
        getActivity2().getIntentExecutor().launchActivity(ShowTextActivity.class, new BundleHelper()
                .putString(ShowTextActivity.KEY_TEXT, text)
                .putInt(ShowTextActivity.KEY_LEVEL, getLevel() + 1)
                .getBundle());
    }

    public void onClickBt(View v){
        launchShowTextActivity(getButtonName());
    }

    public  void onClickTag(View v){
        Logger.w(getPageName(),"onClickTag_before_event", TagTracker.getInstance().mNodes.toString());
        TagTracker.getInstance().trackEvent(TagTracker.TagNode.obtain(
                getLevel() + 1 , getSmallTagName()));
    }

    //----------------------------------
    public String getPageName(){
        return TrackTestUtil.getName(getClass());
    }
    protected String getButtonName(){
        return TrackTestUtil.getButtonName(getClass());
    }
    protected String getSmallTagName(){
        return TrackTestUtil.getSmallTagName(getClass());
    }

    protected abstract int getLevel();
    //----------------------------------------------------

    public void trackThisFragment(){
        TagTracker.getInstance().track(getLevel(), getPageName());
    }
    //life cycle


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Logger.i(getPageName(),"--- onAttach --- : context = " + context);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Logger.i(getPageName(),"--- onAttach --- : activity = " + activity);
      //  Logger.i(getPageName(),"--- onAttach ---");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   Logger.i(getPageName(),"--- onCreate ---");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Logger.i(getPageName(),"--- onCreateView ---");
       return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       // Logger.i(getPageName(),"--- onViewCreated ---");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
       // Logger.i(getPageName(),"--- onDestroyView ---");
    }

    @Override
    public void onDetach() {
        super.onDetach();
      //  Logger.i(getPageName(),"--- onDetach ---");
    }

    //===================================


    @Override
    public void onPause() {
        super.onPause();
      //  Logger.i(getPageName(),"--- onPause ---");
    }

    @Override
    public void onStop() {
        super.onStop();
       // Logger.i(getPageName(),"--- onStop ---");
    }

    @Override
    public void onResume() {
        super.onResume();
       // Logger.i(getPageName(),"--- onResume ---");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // Logger.i(getPageName(),"--- onDestroy ---");
    }

}
