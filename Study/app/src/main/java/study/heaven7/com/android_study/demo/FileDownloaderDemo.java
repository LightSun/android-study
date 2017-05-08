package study.heaven7.com.android_study.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.heaven7.core.util.Logger;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.io.File;

import butterknife.InjectView;
import butterknife.OnClick;
import study.heaven7.com.android_study.BaseActivity;
import study.heaven7.com.android_study.R;
import study.heaven7.com.android_study.third.FileDownloadApi;

/**
 * Created by heaven7 on 2016/2/24.
 */
public class FileDownloaderDemo extends BaseActivity  {

    private static final  String TAG  = "FileDownloaderDemo";

    @InjectView(R.id.pb)
    ProgressBar mPb;

    private boolean mDownloading;
   // @SaveStateField("mDownloadId")
    private int mDownloadId;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_file_download;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        String savePath1 = FileDownloadUtils.getDefaultSaveRootPath() + File.separator + "tmp1";
        Logger.i(TAG,"save path = " + savePath1);
        File f = new File(savePath1);
        if(f.exists()) {
            f.delete();
        }
    }

    @OnClick(R.id.bt_download)
    public void onClickDownload(View v){
        if(!mDownloading) {
            mDownloading = true;
            String savePath1 = FileDownloadUtils.getDefaultSaveRootPath() + File.separator + "tmp1";
            String url = "https://codeload.github.com/LightSun/android-databinding-plugin/zip/master";
            mDownloadId = FileDownloadApi.download(url, savePath1, mListener);
        }else{
            showToast(" is downloading ...");
        }
    }

    @Override
    protected void onDestroy() {
        mDownloading = false;
        FileDownloadApi.pause(mDownloadId);
        super.onDestroy();
    }

    private FileDownloadListener mListener = new FileDownloadListener() {

        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            showToast("pending");
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            // just can test.
            int progress = (int) (soFarBytes * 1f / totalBytes * 100);
            showToast("[ file donwload  ] -- progress:  " + progress + "%");
            mPb.setProgress(progress);
        }

        @Override
        protected void blockComplete(BaseDownloadTask task) {

        }

        @Override
        protected void completed(BaseDownloadTask task) {
            mDownloading = false;
            showToast("[ file donwload  ] -- completed");
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            showToast("[ file donwload  ] -- paused");
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {

        }

        @Override
        protected void warn(BaseDownloadTask task) {

        }
    };
}
