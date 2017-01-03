package study.heaven7.com.android_study.demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import study.heaven7.com.android_study.BaseActivity;
import study.heaven7.com.android_study.R;

/**
 * Created by heaven7 on 2016/5/12.
 */
public class ViewPagerBigImageTestActivity extends BaseActivity {

    @InjectView(R.id.vp)
    ViewPager mVp;

    @Override
    protected int getlayoutId() {
        return R.layout.ac_view_pager_big_image;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        WebViewHelper.readTemplate(this, new WebViewHelper.IloadTemplateCallback() {
            @Override
            public void onSuccess() {
                showPager();
            }

            @Override
            public void onFailed() {
                showToast("load template failed");
            }
        });
    }

    private void showPager() {
        List<String> list = new ArrayList<>();
        list.add("http://bbsfiles.app111.org/forum/201207/16/2202371jz2friyi2qbbgf6.png");
        String sLocalBigImage = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/DCIM/mobile_out/big_image_1.jpg";
        list.add(sLocalBigImage);
        mVp.setAdapter(new AbsWebViewAdapter(this, list, new WebViewHelper.AbsWebViewListener() {
            @Override
            public void onPageFinished(WebView view, String url) {
                showToast("onPageFinished: url = " + url);
            }
        }));
        mVp.setCurrentItem(0);
    }

    static class AbsWebViewAdapter extends PagerAdapter {

        private List<String> mPaths;
        private final WebViewHelper.AbsWebViewListener mListener;
        private final List<WebView> mWebviews;

        public AbsWebViewAdapter(Context context, List<String> mPaths, WebViewHelper.AbsWebViewListener l) {
            this.mPaths = mPaths;
            this.mListener = l;
            this.mWebviews = new ArrayList<>(mPaths.size() * 4 / 3 + 1);
            for (int i = 0, size = mPaths.size(); i < size; i++) {
                mWebviews.add(createWebView(context, i));
            }
        }

        private WebView createWebView(Context context, int position) {
            WebView webView = (WebView) LayoutInflater.from(context).inflate(R.layout.webview_big_image,null);
            webView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mListener.setPosition(position);
            WebViewHelper.initWebView(webView,mListener);
            return webView;
        }

        @Override
        public int getCount() {
            return mPaths.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            WebView webView = mWebviews.get(position);
            View item = instantiateItem(webView, container, position);
            webView.loadDataWithBaseURL(WebViewHelper.BASE_URL, String.format(WebViewHelper.getTemplate(),
                    mPaths.get(position)), "text/html", "utf-8", null);
            container.addView(item);
            return item;
        }

        protected View instantiateItem(WebView webView, ViewGroup container, int position) {
            return webView;
        }
    }

    static class WebViewHelper {

        public static final String BASE_URL = "file:///android_asset/";

        private static String sTemplate;
        private static AsyncTask<Context, Void, String> sTask;

        public static abstract class AbsWebViewListener {

            private int mPosition;

            public void setPosition(int pos) {
                this.mPosition = pos;
            }

            public int getPosition() {
                return mPosition;
            }

            public void onTouch(View v, MotionEvent event) {
            }

            public void onLongClick(View v) {
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
            }

            public void onPageFinished(WebView view, String url) {
            }
        }

        public interface IloadTemplateCallback {
            void onSuccess();

            void onFailed();
        }

        public static void readTemplate(Context context, final IloadTemplateCallback callback) {
            if (sTemplate != null) {
                callback.onSuccess();
                return;
            }
            AsyncTaskCompat.executeParallel( new AsyncTask<Context, Void, String>() {
                @Override
                protected void onPreExecute() {
                    sTask = this;
                }

                @Override
                protected String doInBackground(Context... params) {
                    try {
                        return readTemplateImpl(params[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(String s) {
                    sTask = null;
                    if (s != null) {
                        callback.onSuccess();
                    } else {
                        callback.onFailed();
                    }
                }
            } , context);
        }

        public static String getTemplate() {
            return sTemplate;
        }

        public static void destroy() {
            if (sTask != null) {
                sTask.cancel(true);
                sTask = null;
            }
        }

        private static void initWebView(WebView webView, final AbsWebViewListener l) {
            WebSettings settings = webView.getSettings();
            settings.setLoadsImagesAutomatically(true);
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);
            settings.setLightTouchEnabled(true);
            settings.setBuiltInZoomControls(true);
            settings.setJavaScriptEnabled(true);
            settings.setDisplayZoomControls(false);
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

            webView.setInitialScale(0);
            webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            webView.setHorizontalScrollBarEnabled(false);
            webView.setVerticalScrollBarEnabled(false);
            webView.setBackgroundColor(Color.WHITE);
            //viewer.clearCache(true);
            webView.setFocusable(false);
            webView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    l.onTouch(v, event);
                    return false;
                }
            });
            webView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    l.onLongClick(v);
                    return false;
                }
            });
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    l.onPageFinished(view, url);
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return false;
                }

                @Override
                public void onLoadResource(WebView view, String url) {
                    super.onLoadResource(view, url);
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    l.onPageStarted(view, url, favicon);
                }
            });
        }

        private static String readTemplateImpl(Context context) {
            BufferedReader br = null;
            String tmp;
            StringBuilder sb = new StringBuilder();
            InputStreamReader reader = new InputStreamReader(context.getResources()
                    .openRawResource(R.raw.image_viewer));
            try {
                br = new BufferedReader(reader);
                while ((tmp = br.readLine()) != null) {
                    //template += tmp;
                    sb.append(tmp);
                }
                return (sTemplate = sb.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (br != null) {
                        br.close(); // stop reading
                    }
                } catch (IOException ex) {
                }
            }

        }
    }

}
