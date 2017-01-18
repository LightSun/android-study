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
import android.support.v4.app.FragmentActivity;
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

import study.heaven7.com.android_study.dialog.CommonDialog;

/**
 * common dialog
 * Created by heaven7 on 2017/1/16.
 */

public class CommonDialogFragment extends DialogFragment {

    public static final String TAG = CommonDialogFragment.class.getSimpleName();
    private static final String KEY_LAYOUT_ID = "h7:CommonDialogFragment:layout_id";

    private static final DisplayMetrics DM = new DisplayMetrics();

    private boolean mSaved;
    private Callback mCallback;
    private int mLayoutId;

    public static Builder newBuilder() {
        return new Builder();
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    @Override
    public void onStart() {
        super.onStart();
        //called every turn 'lock off' to 'lock on'
        Logger.i(TAG, "onStart", ""); // called before dialog.onAttachedToWindow.
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(DM);
        final Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mCallback.onSetWindow(window, DM);
        }
        mCallback.onSetDialog(getDialog());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Dialog_NoActionBar);
        if (savedInstanceState != null){
            mLayoutId = savedInstanceState.getInt(KEY_LAYOUT_ID);
        }
        mCallback.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Logger.i(TAG, "onActivityCreated", "");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(mLayoutId, container, false);
        //view.getContext() //maybe ContextThemeWrapper
        mCallback.onBindData(getContext(), view, getArguments(), new ActionProvider(){
            @Override
            public void dismissDialog() {
                 dismiss();
            }
        });
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSaved = false;
    }

    @Override //锁屏也会调用
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_LAYOUT_ID, mLayoutId);
        mSaved = true;
        mCallback.onSaveInstanceState(outState);
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
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    /**
     * the action provider help we handle the dialog
     */
    public static abstract class ActionProvider {

        /**
         * dismiss the dialog. if you want to dismiss , please call this.
         */
        public abstract void dismissDialog();

    }

    /**
     * the callback of CommonDialogFragment.
     */
    public abstract static class Callback extends CommonDialog.Callback {

        public void onSaveInstanceState(Bundle outState) {

        }
        public void onRestoreInstanceState(Bundle savedInstanceState) {

        }

        /**
         * set the dialog, called when create the dialog
         *
         * @param dialog the dialog, often is an instance of {@link CommonDialog}.
         */
        public abstract void onSetDialog(Dialog dialog);

        /**
         * called on start which give a last chance to set Window.
         *
         * @param window the window from dialog
         * @param dm     the current DisplayMetrics
         */
        public abstract void onSetWindow(Window window, DisplayMetrics dm);

        /**
         * bind the data for the view which is the content of fragment
         *  @param context            the context
         * @param view             the view of dialog.v
         * @param provider           the action provider help we handle dialog
         * @param arguments          the arguments which is set by calling {@link android.support.v4.app.Fragment#setArguments(Bundle)}.
         */
        public abstract void onBindData(Context context, View view, Bundle arguments, ActionProvider provider);
    }

    public abstract static class SimpleCallback extends Callback {

        @Override
        public void onSetWindow(Window window, DisplayMetrics dm) {
            WindowManager.LayoutParams wlp = window.getAttributes();
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
        private Callback callback;
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

        public Builder callback(Callback callback) {
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
            fragment.setCallback(callback);
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

        public CommonDialogFragment show(FragmentActivity activity, String tag) {
            return show(activity.getSupportFragmentManager(), tag);
        }

    }

}
