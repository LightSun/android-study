package study.heaven7.com.android_study.third;

import android.app.Application;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;

import java.util.List;

/**
 * Created by heaven7 on 2016/2/24.
 */
public class FileDownloadApi {

    public static void init(Application app){
        FileDownloader.init(app);
    }

    public static void pause(int downloadId){
        FileDownloader.getImpl().pause(downloadId);
    }

    /** single task download .
     * @return download id */
    public static int download(String url,String savePath,FileDownloadListener l){
       return  FileDownloader.getImpl()
                .create(url)
                .setPath(savePath)
                .setListener(l)
                .start();
    }

    /** BaseDownloadTask.pause() */
    public static void downloadMultiTask(List<BaseDownloadTask> tasks,boolean parallel,
                                         int retryTimes,
                                         FileDownloadListener l){
        final FileDownloadQueueSet queue = new FileDownloadQueueSet(l);
        queue.setAutoRetryTimes(retryTimes);
        queue.disableCallbackProgressTimes();
        if(parallel){
            queue.downloadTogether(tasks);
        }else{
            queue.downloadSequentially(tasks);
        }
        queue.start();
    }


}
