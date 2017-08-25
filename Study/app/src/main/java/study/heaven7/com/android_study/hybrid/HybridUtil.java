package study.heaven7.com.android_study.hybrid;

import android.os.Build;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import com.heaven7.core.util.Logger;


import study.heaven7.com.android_study.util.JsonHelper;

/**
 * Created by heaven7 on 2017/8/25 0025.
 */

public class HybridUtil {
    public static final String SCHEME       = "class100";
    public static final String KEY_PARAM    = "param";
    public static final String KEY_CALLBACK = "callback";


    public static String getJsScript(String callback, String data){
        return "Hybrid.callback(" + new JsonHelper()
                .put("callback", callback)
                .put("data", data).toJson() + ")";

    }

    public static void executeJavascript(WebView wv, String callback, String data){
        String script = getJsScript(callback, data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            wv.evaluateJavascript(script, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    Logger.i("HybridUtil","onReceiveValue","value = " + value);
                   /* if (!"true".equals(value) && "back".equals(param.tagname))
                        onBackPressed();*/
                }
            });
        }else{
            wv.loadUrl("javascript:" + script);
        }
    }

}
