package study.heaven7.com.android_study.demo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.InjectView;
import butterknife.OnClick;
import study.heaven7.com.android_study.BaseActivity;
import study.heaven7.com.android_study.R;
import study.heaven7.com.android_study.util.ViewUtils;
import study.heaven7.com.android_study.view.DragFlowLayout;

/**
 * Created by heaven7 on 2016/8/1.
 */
public class DragFlowLayoutTest2 extends BaseActivity {

    @InjectView(R.id.drag_flowLayout)
    DragFlowLayout mDragflowLayout;

    private int mIndex;
    @Override
    protected int getlayoutId() {
        return R.layout.ac_drag_flow_test2;
    }

    @Override
    protected void initView() {
        mDragflowLayout.setCallback(new DragFlowLayout.Callback() {
            @Override
            public void setChildByDragState(View view, boolean isDragState) {
                TestBean bean = (TestBean) view.getTag();
                //实际应用中，我们可能控制某些item不能拖拽
                view.findViewById(R.id.iv_close).setVisibility(
                        isDragState && bean.draggable ? View.VISIBLE : View.INVISIBLE);
            }

            @NonNull
            @Override
            public View copyChildView(View v, int index) {
                final TestBean bean = (TestBean) v.getTag();
                return createItemView(bean);
            }

            @Override
            public void setWindowViewByChild(View windowView, View child) {
                final TestBean bean = (TestBean) child.getTag();
                windowView.setTag(bean);

                TextView tv = (TextView) windowView.findViewById(R.id.tv_text);
                tv.setText(bean.text);
            }
            @Override
            public View createWindowView(View touchChildView) {
                final TestBean bean = (TestBean) touchChildView.getTag();
                return createWindowItemView(bean);
            }
            @Override
            public boolean performClick(DragFlowLayout dragFlowLayout, View child, MotionEvent event, boolean inDragState) {
                boolean performed = inDragState && ViewUtils.isViewUnderInScreen(child.findViewById(R.id.iv_close),
                        (int) event.getRawX(),(int) event.getRawY());
                if(performed){
                    dragFlowLayout.removeView(child);
                }
                //点击事件
                return performed;
            }

            @Override
            public boolean isChildDraggable(View child) {
                TestBean bean = (TestBean) child.getTag();
                return bean.draggable;
            }
        });
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }
    @OnClick(R.id.bt_done)
    public void onClickDone(View v){
        mDragflowLayout.setDragState(false, true);
    }

    @OnClick(R.id.bt_add)
    public void onClickAdd(View v){
        final TestBean bean = new TestBean("test_" + (mIndex++));
        if(mDragflowLayout.getChildCount()==0) {
            //为了测试，设置第一个条目不准拖拽
            bean.draggable = false;
            mDragflowLayout.addView(createItemView(bean));
        }else{
            mDragflowLayout.addView(createItemView(bean), 1);
        }
    }
    @OnClick(R.id.bt_remove_center)
    public void onClickRemoveCenter(View v){
        final View itemView = createItemView(new TestBean("test_" + (mIndex++)));
        final int count = mDragflowLayout.getChildCount();
        if(count == 0){
            return;
        }
        if(count <=2) {
            mDragflowLayout.removeViewAt(count-1);
        }else{
            mDragflowLayout.removeViewAt(count-2);
        }
    }

    private View createWindowItemView(TestBean bean){
        final View view = View.inflate(this, R.layout.item_drag_flow, null);
        view.setTag(bean);

        TextView tv = (TextView) view.findViewById(R.id.tv_text);
        tv.setText(bean.text);
        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDragflowLayout.releaseDrag();
            }
        });
        return view;
    }
    public View createItemView(final TestBean bean){
        final View view = View.inflate(this, R.layout.item_drag_flow, null);
        view.setTag(bean);

        TextView tv = (TextView) view.findViewById(R.id.tv_text);
        tv.setText(bean.text);

        final ImageView iv_close = (ImageView) view.findViewById(R.id.iv_close);
        iv_close.setVisibility(View.INVISIBLE);
        return view;
    }

    private static class  TestBean{
        String text;
        boolean draggable = true;
        public TestBean(String text) {
            this.text = text;
        }
    }
}
