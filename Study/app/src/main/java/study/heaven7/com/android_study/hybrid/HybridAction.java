package study.heaven7.com.android_study.hybrid;

import android.content.Context;

/**
 * Created by heaven7 on 2017/8/25 0025.
 */

public interface HybridAction {

    void onAction(Context context, String param, String callback, int hashOfWebView);

}
