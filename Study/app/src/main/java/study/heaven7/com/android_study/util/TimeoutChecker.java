package study.heaven7.com.android_study.util;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 功能描述
 */
public class TimeoutChecker<T> {
    private static final int THRESHOLD_TIME_OUT = 10000; //10秒超时
    private final ArrayMap<T, InternalRunner> mCache;
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static ArrayList<String> mWhiteRoutes;//白名单，不需要超时检测

    static {
        mWhiteRoutes = new ArrayList<>();
        mWhiteRoutes.add("gate.gateHandler.queryEntry");
        mWhiteRoutes.add("connector.entryHandler.entry");
    }

    public TimeoutChecker() {
        this.mCache = new ArrayMap<>(5);
    }

    /**
     * do check timeout
     */
    public void checkTimeout(String route, T key, ITimeoutError observer) {
        //消息路由白名单
        if (mWhiteRoutes.contains(route)) return;
        Log.i("TimeoutChecker", " checkTimeout key=" + key);
        final InternalRunner runner = new InternalRunner(key, observer);
        mCache.put(key, runner);
        mainHandler.postDelayed(runner, THRESHOLD_TIME_OUT);
    }

    /**
     * give up for check timeout by target T
     */
    public void giveUpCheck(T key) {
        final InternalRunner runner = mCache.remove(key);
        if (runner != null) {
            mainHandler.removeCallbacks(runner);
        }
    }

    public void giveUpAll() {
        final Collection<InternalRunner> values = mCache.values();
        for (InternalRunner runner : values) {
            mainHandler.removeCallbacks(runner);
        }
        mCache.clear();
    }

    private class InternalRunner implements Runnable {

        private final T item;
        private final ITimeoutError mObserver;

        public InternalRunner(T item, ITimeoutError observer) {
            this.item = item;
            this.mObserver = observer;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            InternalRunner runner = (InternalRunner) o;
            return item.equals(runner.item);
        }

        @Override
        public int hashCode() {
            return item.hashCode();
        }

        @Override
        public void run() {
            mCache.remove(item);
            mObserver.timeoutException(item);
        }
    }

    public interface ITimeoutError<T> {
        void timeoutException(T obj);
    }


}
