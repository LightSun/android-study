package study.heaven7.com.android_study.framework;

import android.content.Context;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by heaven7 on 2017/8/9 0009.
 */

public abstract class UiManager {

   /* @BindView(R.id.vg_top)
    ViewGroup mVg_top;*/

    private final Context mContext;

    public UiManager(View itemView){
        this.mContext = itemView.getContext();
        ButterKnife.inject(this, itemView);
    }

    public Context getContext(){
        return mContext;
    }

    public abstract  int getLayoutId();
}
