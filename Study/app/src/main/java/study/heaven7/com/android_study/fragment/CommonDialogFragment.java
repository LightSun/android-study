package study.heaven7.com.android_study.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.heaven7.core.util.Logger;
import com.heaven7.core.util.ViewHelper;

import study.heaven7.com.android_study.dialog.CommonDialog;

/**
 * common dialog
 * Created by heaven7 on 2017/1/16.
 */

public class CommonDialogFragment extends DialogFragment {

    public static final String TAG = CommonDialogFragment.class.getSimpleName();
    private static final DisplayMetrics DM = new DisplayMetrics();
    private boolean mSaved;
    private ICallback mCallback;
    private int mLayoutId;

    private ViewHelper mHelper;

    public static Builder newBuilder(){
        return new Builder();
    }

    @Override
    public void onStart() {
        super.onStart();
        Logger.i(TAG, "onStart", ""); // called before dialog.onAttachedToWindow.
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(DM);
        final Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mCallback.onSetWindow(window);

            final WindowManager.LayoutParams wlp = window.getAttributes();
            mCallback.onSetWindowLayoutParams(wlp, DM);

            window.setAttributes(wlp);
        }
        mCallback.onSetDialog(getDialog()); //cancelable
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Dialog_NoActionBar);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(mLayoutId, container, false);
        mHelper = new ViewHelper(view);
        mCallback.onBindData(inflater.getContext(), mHelper, savedInstanceState, getArguments());
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSaved = false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSaved = true;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (mSaved) {
            Log.w(TAG, "called show():  but onSaveInstanceState() was called.");
            return;
        }
        super.show(manager, tag);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);
        return new CommonDialog(getContext(), getTheme()).callback(mCallback);
    }

    @Override
    public void onDestroyView() {
        Logger.i(TAG, "onDestroyView", ""); //called after dialog#dismiss.
        mHelper = null;
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    public interface ICallback extends CommonDialog.Callback {

        void onSetWindowLayoutParams(WindowManager.LayoutParams wlp, DisplayMetrics dm);

        void onSetDialog(Dialog dialog);

        void onSetWindow(Window window);

        void onBindData(Context context, ViewHelper helper, Bundle savedInstanceState, Bundle arguments);
    }

    public abstract static class SimpleCallback implements CommonDialogFragment.ICallback {
        @Override
        public void onSetWindowLayoutParams(WindowManager.LayoutParams wlp, DisplayMetrics dm) {
            wlp.width = dm.widthPixels * 4 / 5;
            wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            wlp.gravity = Gravity.CENTER;
        }

        @Override
        public void onSetDialog(Dialog dialog) {
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
        }

        @Override
        public void onSetWindow(Window window) {
        }

        @Override
        public void beforeDismiss(View view) {
            // Logger.i(TAG, "beforeDismiss", "" + view);
        }

        @Override
        public void afterShow(View view) {
            //Logger.i(TAG, "afterShow", "" + view);
        }
    }

    public static class Builder {
        private int layoutId;
        private boolean retain;
        private ICallback callback;
        private Bundle args;
        private CommonDialogFragment mFragment;

        public Builder layoutId(@LayoutRes int layoutId) {
            this.layoutId = layoutId;
            return this;
        }

        public Builder retain(boolean retain) {
            this.retain = retain;
            return this;
        }

        public Builder callback(ICallback callback) {
            this.callback = callback;
            return this;
        }

        public Builder arguments(Bundle args) {
            this.args = args;
            return this;
        }

        public Builder build() {
            if (callback == null) {
                throw new IllegalStateException("callback can't be null ! you must set callback first.");
            }
            if (layoutId <= 0) {
                throw new IllegalStateException("layoutId must > 0 ! you must set layoutId first.");
            }
            CommonDialogFragment fragment = new CommonDialogFragment();
            fragment.setRetainInstance(retain);
            fragment.mLayoutId = layoutId;
            fragment.mCallback = callback;
            fragment.setArguments(args);
            this.mFragment = fragment;
            return this;
        }

        public CommonDialogFragment show(FragmentManager fm, String tag) {
            //final CommonDialogFragment mFragment = this.mFragment;
            if (mFragment == null) {
                throw new IllegalStateException("you must call build() first");
            }
            mFragment.show(fm, tag);
            return mFragment;
        }
    }
}
