package study.heaven7.com.android_study.demo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import com.heaven7.core.util.Logger;
import com.heaven7.core.util.MainWorker;
import com.heaven7.core.util.ViewHelper;

import java.lang.ref.WeakReference;
import java.util.Random;

import butterknife.OnClick;
import study.heaven7.com.android_study.BaseActivity;
import study.heaven7.com.android_study.R;
import study.heaven7.com.android_study.fragment.CommonDialogFragment;

/**
 * Created by heaven7 on 2017/1/16.
 */

public class TestCommonAdmireDialog extends BaseActivity {

    private CommonDialogFragment fragment;
    private final RandomTextTask mTask = new RandomTextTask();

    @Override
    protected int getlayoutId() {
        return R.layout.ac_state_bar;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void onDestroy() {
        dismissFragment();
        super.onDestroy();
    }

    @OnClick(R.id.bt_back)
    public void onClickTrigger(View v) {
        fragment = CommonDialogFragment.newBuilder()
                .layoutId(R.layout.view_admire_dialog)
                .callback(new CallbackImpl())
                .build()
                .show(getSupportFragmentManager(), "dialog-fragment");
    }


    private void dismissFragment() {
        if (fragment != null && fragment.isVisible()) {
            fragment.dismissAllowingStateLoss();
            fragment = null;
        }
    }

    private void startRandomTextTask(TextView tv) {
        cancelTask();
        mTask.setTextView(tv).start();
    }

    private void cancelTask() {
        mTask.cancel();
    }

    private class CallbackImpl extends CommonDialogFragment.SimpleCallback {

        @Override
        public void onBindData(Context context, ViewHelper helper, Bundle savedInstanceState, Bundle arguments) {
            helper.setOnClickListener(R.id.admire_cancel_img, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissFragment();
                }
            });
        }

        @Override
        public void setupDialog(Dialog dialog) {
            super.setupDialog(dialog);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    cancelTask();
                }
            });
        }

        @Override
        public void afterShow(View view) {
            super.afterShow(view);
            TextView et_price = (TextView) view.findViewById(R.id.admire_edit_price);
            startRandomTextTask(et_price);
        }
    }

    private static class RandomTextTask implements Runnable {

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
            Logger.i("RandomTextTask", "onAnimationUpdate", "cancelled");
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


}
