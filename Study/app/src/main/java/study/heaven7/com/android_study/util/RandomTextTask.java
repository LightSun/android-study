package study.heaven7.com.android_study.util;

import android.os.SystemClock;
import android.widget.TextView;

import com.heaven7.core.util.MainWorker;

import java.lang.ref.WeakReference;
import java.util.Random;

/**
 * random text task. can not used for adapter item.
 * @author heaven7
 */
public class RandomTextTask implements Runnable {

    private static final Random sRandom = new Random();
    private WeakReference<TextView> mWeakView;
    private final int mMin;
    private final int mMax;

    private long mDuration = 6000;
    private long mLastTime;
    private long mConsumeTime;

    public RandomTextTask() {
        this(10, 100);
    }

    public RandomTextTask(int min, int max) {
        this.mMin = min;
        this.mMax = max;
    }

    public RandomTextTask setTextView(TextView tv) {
        mWeakView = new WeakReference<TextView>(tv);
        return this;
    }

    public RandomTextTask setDuration(long duration) {
        mDuration = duration;
        return this;
    }

    public static int random(int min, int max) {
        return min + sRandom.nextInt(max - min);
    }

    public void start() {
        reset();
        mLastTime = SystemClock.elapsedRealtime();
        MainWorker.removePreviousAndPostDelay(randomDelayTime(sRandom), this);
    }

    public void reset() {
        mLastTime = 0;
        mConsumeTime = 0;
    }

    public void cancel() {
        MainWorker.remove(this);
        reset();
      //  Logger.i("RandomTextTask", "onAnimationUpdate", "cancelled");
    }

    protected int randomDelayTime(Random r) {
        return random(200, 800);
    }

    protected void onSetText(TextView view, int animatedValue) {
        final int v1 = animatedValue + sRandom.nextInt(animatedValue);
        view.setText(String.valueOf(v1));
    }

    @Override
    public void run() {
        final long current = SystemClock.elapsedRealtime();
        mConsumeTime += current - mLastTime;
        mLastTime = current;

        final TextView view = mWeakView.get();
        if (mConsumeTime >= mDuration || view == null) {
            //time reach or view is recycled.
            cancel();
            return;
        }
        onSetText(view, random(mMin, mMax));
        MainWorker.postDelay(randomDelayTime(sRandom), this);
    }
}
