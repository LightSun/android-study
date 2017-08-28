package com.classroom100.android.util;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.class100.lib.msc.MscHelper;
import com.class100.lib.msc.helper.IseHelper;
import com.classroom100.android.R;
import com.classroom100.android.activity.BaseActivity;
import com.classroom100.android.activity.helper.answer.AnswerFields;
import com.classroom100.android.api.model.AnswerData;
import com.classroom100.android.common.Class100Toaster;
import com.classroom100.android.design.impl.LFComponent.IseLFComponent;
import com.classroom100.android.ise.AnswerIseListenerImpl;
import com.google.gson.Gson;
import com.heaven7.core.util.Logger;

import java.lang.ref.WeakReference;


/**
 * a class which communicate with js.
 * Created by heaven7 on 2017/7/6 0006.
 */

public final class JsInterflow {

    private static final int CODE_SUCCESS                  = 0;
    private static final int CODE_ERROR                    = 1;
    private static final int CODE_NO_VOICE                 = 2;
    private static final int CODE_PERMISSION_ERROR_AUDIO   = 3;
    private static final int CODE_PERMISSION_ERROR_SD      = 4;

    private static final String TAG = "JsInterflow";

    private final BaseActivity mActivity;
    private final IseHelper mIseHelper;
    private final WeakReference<WebView> mWebView;

    public JsInterflow(BaseActivity mActivity, WebView mWebView) {
        this.mActivity = mActivity;
        this.mIseHelper = MscHelper.getIseHelper();
        this.mWebView = new WeakReference<WebView>(mWebView);
        mActivity.getLifecycleComponentManager().registerComponent(new IseLFComponent(mIseHelper));
    }

    @JavascriptInterface
    public boolean startEveluate(String text, String questionId, String qItemId,
                                 String answerId, int type) {
        if (!com.heaven7.android.util2.NetworkCompatUtil.hasConnectedNetwork(mActivity)) {
            mActivity.getCommonToaster().show(R.string.notice_no_network);
            return false;
        }
        Logger.d(TAG,"startEveluate","text = " + text + ", questionId = " + questionId +
                ", qItemId = " + qItemId + ", answerId = " + answerId);
        MscHelper.getIseHelper().startEvaluating(text,
                new AnswerIseListenerImpl(mActivity, new AnswerFields(questionId, qItemId, answerId, type),
                        new InternalCallbackImpl(new SimpleJsCallback(mWebView)))
        );
        return true;
    }

    @JavascriptInterface
    public void stopEvaluate(){
        Logger.d(TAG,"stopEvaluate","");
        if(mIseHelper.isEvaluating()) {
            MscHelper.getIseHelper().stopEvaluating();
        }
    }

    private Class100Toaster getToaster() {
        return mActivity.getCommonToaster();
    }

    private class InternalCallbackImpl extends AnswerIseListenerImpl.SimpleEvaluateCallback{

        private final JsCallback mCallback;

        public InternalCallbackImpl(JsCallback mCallback) {
            this.mCallback = mCallback;
        }

        @Override
        public void onSuccess(AnswerData result) {
            Logger.i(TAG, "onSuccess", "result = " + result);
            mCallback.onResult(CODE_SUCCESS, new Gson().toJson(result));
        }

        @Override
        public void onError(String msg) {
            getToaster().show(msg);
            mCallback.onResult(CODE_ERROR, msg);
        }

        @Override
        public void onAudioSaved(String file) {
            super.onAudioSaved(file);
            mCallback.onAudioSaved(file);
        }

        @Override
        public void onVolumeChanged(int newVolume, byte[] data) {
            super.onVolumeChanged(newVolume, data);
            mCallback.onVolumeChanged(newVolume);
        }
        @Override
        public void onNoVoice() {
            mCallback.onResult(CODE_NO_VOICE, "");
        }
        @Override
        public void onRequestPermissionAudioError() {
            super.onRequestPermissionAudioError();
            mCallback.onResult(CODE_PERMISSION_ERROR_AUDIO, "");
        }
        @Override
        public void onRequestPermissionSDError() {
            super.onRequestPermissionSDError();
            mCallback.onResult(CODE_PERMISSION_ERROR_SD, "");
        }
    }

    public interface JsCallback{
        void onResult(int code, String result);
        void onAudioSaved(String file);
        void onVolumeChanged(int newVolume);
    }

    public static class SimpleJsCallback implements JsCallback{

        private final WeakReference<WebView> mWeakWeb;

        public SimpleJsCallback(WeakReference mWebView) {
            this.mWeakWeb = mWebView;
        }
        @Override
        public void onResult(int code, String result) {
            WebView webView = mWeakWeb.get();
            if(webView == null){
                return;
            }
            webView.loadUrl("javascript:onResult(" + code + ","+ result + ")");
        }

        @Override
        public void onAudioSaved(String file) {
            WebView webView = mWeakWeb.get();
            if(webView == null){
                return;
            }
            webView.loadUrl("javascript:onAudioSaved(" + file +")");
        }

        @Override
        public void onVolumeChanged(int newVolume) {
            WebView webView = mWeakWeb.get();
            if(webView == null){
                return;
            }
            webView.loadUrl("javascript:onVolumeChanged(" + newVolume +")");
        }
    }

}
