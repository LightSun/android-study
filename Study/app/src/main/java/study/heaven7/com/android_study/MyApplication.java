package study.heaven7.com.android_study;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import study.heaven7.com.android_study.third.FileDownloadApi;

/**
 * Created by heaven7 on 2016/2/24.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
       // RequestManager.init(this);
         /* Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());*/

        FileDownloadApi.init(this);

        Realm.init(this);
        //默认配置
       // RealmConfiguration config = new RealmConfiguration.Builder().build();
       // Realm.setDefaultConfiguration(config);
         //自定义配置
        RealmConfiguration config = new  RealmConfiguration.Builder()
                .name("myRealm.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
