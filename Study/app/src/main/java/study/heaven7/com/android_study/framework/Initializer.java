package study.heaven7.com.android_study.framework;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by heaven7 on 2017/8/9 0009.
 */

public interface Initializer {

    void onInitialize(Context context, @Nullable Bundle savedInstanceState);
}
