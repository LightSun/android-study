package study.heaven7.com.android_study.media;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;

import com.heaven7.core.util.Logger;

/**
 * Created by heaven7 on 2017/7/13 0013.
 */

public class MediaPlaybackService extends Service{

    private static final String TAG = "MediaPlaybackService";
    private MediaSessionCompat mMediaSessionCompat;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaSessionCompat = new MediaSessionCompat(this, "MediaButtonReceiver");
        mMediaSessionCompat.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPrepare() {
                super.onPrepare();
                Logger.i(TAG,"onPrepare","");
            }

            @Override
            public void onPlay() {
                super.onPlay();
                Logger.i(TAG,"onPlay","");
            }

            @Override
            public void onPause() {
                super.onPause();
                Logger.i(TAG,"onPause","");
            }

            @Override
            public void onStop() {
                super.onStop();
                Logger.i(TAG,"onStop","");
            }

            @Override
            public void onSeekTo(long pos) {
                super.onSeekTo(pos);
                Logger.i(TAG,"onSeekTo","");
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(mMediaSessionCompat, intent);
        return super.onStartCommand(intent, flags, startId);
    }
}
