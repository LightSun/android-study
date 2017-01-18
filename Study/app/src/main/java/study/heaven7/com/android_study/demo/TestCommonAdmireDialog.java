package study.heaven7.com.android_study.demo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.heaven7.core.util.Logger;
import com.heaven7.core.util.ViewHelper;

import butterknife.OnClick;
import study.heaven7.com.android_study.BaseActivity;
import study.heaven7.com.android_study.R;
import study.heaven7.com.android_study.fragment.CommonDialogFragment;
import study.heaven7.com.android_study.util.RandomTextTask;

/**
 * Created by heaven7 on 2017/1/16.
 */

public class TestCommonAdmireDialog extends BaseActivity {

    private static final String TAG = "TestCommonAdmireDialog";
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
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            Logger.i(TAG, "onSaveInstanceState", "");
        }
        @Override
        public void onRestoreInstanceState(Bundle savedInstanceState) {
            super.onRestoreInstanceState(savedInstanceState);
            Logger.i(TAG, "onRestoreInstanceState", "");
        }

        @Override
        public void onBindData(Context context, View view, Bundle arguments, final CommonDialogFragment.ActionProvider provider) {
            new ViewHelper(view).setOnClickListener(R.id.admire_cancel_img, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    provider.dismissDialog();
                }
            });
        }

        @Override
        public void onSetDialog(Dialog dialog) {
            super.onSetDialog(dialog);
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

}
