package study.heaven7.com.android_study.hybrid.action;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;

import com.heaven7.core.util.Logger;

import study.heaven7.com.android_study.hybrid.HybridAction;

/**
 * finish.activity.
 * Created by heaven7 on 2017/8/25 0025.
 */

public class FinishHybridAction implements HybridAction {

    @Override
    public void onAction(Context context, String param, String callback, int hashOfWebView) {
        while ( !(context instanceof Activity)){
            if(context instanceof ContextWrapper){
                context = ((ContextWrapper) context).getBaseContext();
            }else{
                Logger.w("FinishHybridAction","onAction","context isn't activity or ContextWrapper. \n\t" +
                        "context = " + context.getClass().getName());
                break;
            }
        }
        if(context instanceof Activity) {
            ((Activity) context).finish();
        }
    }
}
