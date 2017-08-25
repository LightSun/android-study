package study.heaven7.com.android_study.hybrid;

import android.annotation.TargetApi;
import android.net.Uri;
import android.support.v4.util.ArrayMap;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.heaven7.core.util.Logger;

/**
 * Created by Administrator on 2017/8/25 0025.
 */

public class HybridWebViewClient extends WebViewClient {

    private static ArrayMap<String, Class<? extends HybridAction>> sTagMap;

    static{
        sTagMap = new ArrayMap<>();
        //TODO should register
    }

    @TargetApi(21)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return super.shouldInterceptRequest(view, request);
    }

    @TargetApi(24)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        return super.shouldInterceptRequest(view, url);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        //class100://tag /xxx?param=xxx&callback=xxx
        Uri uri = Uri.parse(url);
        String scheme = uri.getScheme();
        if(HybridUtil.SCHEME.equals(scheme)){
            String tag = uri.getHost();
            if(!hasAction(tag)){
                return super.shouldOverrideUrlLoading(view, url);
            }
            String param = uri.getQueryParameter(HybridUtil.KEY_PARAM);
            String callback = uri.getQueryParameter(HybridUtil.KEY_CALLBACK);
            if(!dispatchAction(view, tag, param, callback)){
                Logger.w("HybridWebViewClient","shouldOverrideUrlLoading","dispatch failed. url = " + url);
            }
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    private boolean dispatchAction(WebView view, String tag, String param, String callback) {
        try {
            HybridAction action = sTagMap.get(tag).newInstance();
            action.onAction(view.getContext(), param, callback, view.hashCode());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean hasAction(String tag){
        return sTagMap.get(tag) != null;
    }
}
