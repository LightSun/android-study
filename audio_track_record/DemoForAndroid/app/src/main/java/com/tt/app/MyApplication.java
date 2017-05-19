package com.tt.app;

import android.app.Application;

/**
 * Created by cj on 2016/12/27.
 */

public class MyApplication extends Application implements Thread.UncaughtExceptionHandler {

    /**
     * 全局Context，原理是因为Application类是应用最先运行的，所以在我们的代码调用时，该值已经被赋值过了
     */
    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static MyApplication getApplication() {
        return mInstance;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable e) {
        thread.setDefaultUncaughtExceptionHandler( this);
    }
}
