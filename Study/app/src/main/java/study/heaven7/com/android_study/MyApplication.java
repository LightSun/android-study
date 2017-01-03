package study.heaven7.com.android_study;

import android.app.Application;

import study.heaven7.com.android_study.third.FileDownloadApi;

/**
 * Created by heaven7 on 2016/2/24.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
       // RequestManager.init(this);
        FileDownloadApi.init(this);

       /* Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());*/
    }
}
