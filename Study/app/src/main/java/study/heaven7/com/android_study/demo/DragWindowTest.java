package study.heaven7.com.android_study.demo;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import com.heaven7.core.util.MainWorker;

import study.heaven7.com.android_study.BaseActivity;
import study.heaven7.com.android_study.R;

/**
 * Created by heaven7 on 2016/8/4.
 */
public class DragWindowTest extends BaseActivity {

    private static final String TAG = "DragWindowTest";
    private WindowManager mWM;
    private View view;
    private WindowManager.LayoutParams params;
    private int mTouchSlop;

    @Override
    protected int getlayoutId() {
        return R.layout.ac_drag_flow;
    }

    @Override
    protected void initView() {
        mWM = getWindowManager();
        mTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            MainWorker.postDelay(500, new Runnable() {
                @Override
                public void run() {
                    //startService(new Intent(getApplicationContext(), DragWindowService.class));
                    showMyToast();
                }
            });
        }
    }

    /**
     * 显示自定义吐丝: http://blog.csdn.net/g707175425/article/details/46471937
     */
    private void showMyToast() {
        if(view != null ){
            mWM.removeView(view);
            view = null;
        }
        view = View.inflate(this, R.layout.item_drag_flow, null);
        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("iv_close");
            }
        });

        view.setOnTouchListener(new DragTouchListener());//给吐丝添加拖拽事件
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.gravity = Gravity.LEFT | Gravity.TOP; //这是窗体的原点位置,如果设置为CENTER默认,远点会在屏幕中间
        params.x = 150 ;
        params.y = 220;
      //  params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;//需要权限SYSTEM_ALERT_WINDOW;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;      //focusable不能去,如果去了后面的窗口就都不能操作了
        mWM.addView(view, params);//将View对象添加到窗体上显示
    }

    private class DragTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
            params.x = (int) event.getRawX() - view.getMeasuredWidth() / 2;
            Log.i(TAG, "X = "+ event.getX() + " ,RawX = " + event.getRawX());
            // 减25为状态栏的高度
            params.y = (int) event.getRawY() - view.getMeasuredHeight() / 2 - 25;
            Log.i(TAG, "Y = "+ event.getY()+" ,RawY =" + event.getRawY());
            Log.i(TAG, "left = "+ view.getLeft()+" ,RawY =" + event.getRawY());
            // 刷新
            mWM.updateViewLayout(view, params);
            //onTouch和onClick冲突 ,可通过v.performClick()去解决
            //v.performClick()
            return false; // 此处必须返回false，否则OnClickListener获取不到监听
        }
    }

}
