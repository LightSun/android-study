package study.heaven7.com.android_study.framework;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.heaven7.core.util.Toaster;

/**
 *
 资源预处理， 逻辑处理.  动画处理. 生命周期组件. dialog处理.
 数据填充(async). loading
 */
public interface AppComponentContext {

    /*
     * get the layout id.
     * @return the layout id
     */
  //   int getLayoutId(); //moved to UiManager

    /**
     * get the toaster.
     * @return  the {@link IToast}
     */
    IToast getToaster();

    UiManager getUiManager();

    Initializer getInitializer();
    //net. db. anim . logic ,data?

    /*
     * on initialize
     * @param context the context
     * @param savedInstanceState the bundle of save instance
     */
   // void onInitialize(Context context, @Nullable Bundle savedInstanceState);



}
