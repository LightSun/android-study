package study.heaven7.com.android_study.bigimage;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.heaven7.core.util.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import study.heaven7.com.android_study.BaseActivity;
import study.heaven7.com.android_study.R;

/**
 * Created by heaven7 on 2016/5/12.
 */
public class ImageViewer extends BaseActivity {

    public static final String KEY_IMAGE_PATH    = "image_path";

    private static final String TAG = "ImageViewer";

    private WebView viewer;
    private WebSettings settings;

    private static final String BASE_URL = "file:///android_asset/";
    private String template = "";
    private String imageUrl;


    @Override
    protected int getlayoutId() {
        return R.layout.image_viewer;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        viewer = (WebView) findViewById(R.id.viewer);
        settings = viewer.getSettings();

        imageUrl = getIntent().getStringExtra(KEY_IMAGE_PATH);
        if (imageUrl == null) {
            imageUrl = "";
        }

        initViewer();
        readTemplateString();

        if (imageUrl != null && imageUrl.length() > 1) {
            viewer.loadDataWithBaseURL(BASE_URL, String.format(template, imageUrl), "text/html", "utf-8", null);
        }
    }

  /*  @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_viewer);

        viewer = (WebView) findViewById(R.id.viewer);
        settings = viewer.getSettings();

        imageUrl = getIntent().getStringExtra(KEY_IMAGE_PATH);
        if (imageUrl == null) {
            imageUrl = "";
        }

        initViewer();
        readTemplateString();

        if (imageUrl != null && imageUrl.length() > 1) {
            viewer.loadDataWithBaseURL(BASE_URL, String.format(template, imageUrl), "text/html", "utf-8", null);
        }
    }*/


    private void initViewer() {
        settings.setLoadsImagesAutomatically(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setLightTouchEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setJavaScriptEnabled(true);
        settings.setDisplayZoomControls(false);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        viewer.setInitialScale(0);
        viewer.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        viewer.setHorizontalScrollBarEnabled(false);
        viewer.setVerticalScrollBarEnabled(false);
        viewer.setBackgroundColor(0x000000);
        //viewer.clearCache(true);
        viewer.setFocusable(true);
        viewer.setFocusableInTouchMode(true);
       // viewer.setClickable(true);

        viewer.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Logger.i(TAG, "onPageFinished", "url = " + url);
            }
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });
        viewer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showToast("onTouch");
                return false;
            }
        });
        viewer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showToast("onLongClick");
                return false;
            }
        });
    }

    private void readTemplateString() {
        BufferedReader br = null;
        String tmp ;
        StringBuilder sb = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(getResources().openRawResource(R.raw.image_viewer));
        try {
            br = new BufferedReader(reader);
            while ((tmp = br.readLine()) != null) {
                //template += tmp;
                sb.append(tmp);
            }
            template = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(br!=null) {
                    br.close(); // stop reading
                }
            } catch (IOException ex) {
            }
        }

    }

}
