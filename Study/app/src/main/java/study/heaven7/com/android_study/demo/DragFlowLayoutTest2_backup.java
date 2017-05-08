package study.heaven7.com.android_study.demo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.InjectView;
import butterknife.OnClick;
import study.heaven7.com.android_study.BaseActivity;
import study.heaven7.com.android_study.R;
import study.heaven7.com.android_study.view.DragFlowLayout_backup;

/**
 * Created by heaven7 on 2016/8/1.
 */
public class DragFlowLayoutTest2_backup extends BaseActivity {

    @InjectView(R.id.drag_flowLayout)
    DragFlowLayout_backup mDragflowLayout;

    private int mIndex;
    @Override
    protected int getLayoutId() {
        return R.layout.ac_drag_flow_test2;
    }

    @Override
    protected void initView() {
        mDragflowLayout.setDragCallback(new DragFlowLayout_backup.ICallback() {
            @Override
            public boolean isDraggable(DragFlowLayout_backup parent, View child) {
                return true;
            }
            @NonNull
            @Override
            public View copyChildView(View v, int index) {
                final TestBean bean = (TestBean) v.getTag();
                return createItemView(bean);
            }
            @Override
            public void onDragEnd(View releasedChild) {

            }
        });
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }
    private void showOrHideClose(boolean show){
        View view;
        for(int i=0, size = mDragflowLayout.getChildCount(); i<size ;i++){
            view = mDragflowLayout.getChildAt(i);
            TestBean bean = (TestBean) view.getTag();
            view.findViewById(R.id.iv_close).setVisibility(
                    show && bean.showClose ? View.VISIBLE : View.INVISIBLE
            );
        }
    }
    @OnClick(R.id.bt_done)
    public void onClickDone(View v){
        showOrHideClose(false);
        mDragflowLayout.cancelDrag();
        mDragflowLayout.setDraggable(false);
    }
    @OnClick(R.id.bt_add)
    public void onClickAdd(View v){
        final View itemView = createItemView(new TestBean("test_" + (mIndex++)));
        if(mDragflowLayout.getChildCount()==0) {
            mDragflowLayout.addView(itemView);
        }else{
            mDragflowLayout.addView(itemView, 1);
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

    public View createItemView(TestBean bean){
        final View view = View.inflate(this, R.layout.item_drag_flow, null);
        view.setTag(bean);

        TextView tv = (TextView) view.findViewById(R.id.tv_text);
        tv.setText(bean.text);

        final ImageView iv_close = (ImageView) view.findViewById(R.id.iv_close);
        iv_close.setVisibility(View.INVISIBLE);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDragflowLayout.removeView(view);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showOrHideClose(true);
                mDragflowLayout.setDraggable(true);
                return false;
            }
        });
        return view;
    }

    static class  TestBean{
        String text;
        boolean showClose = true;
        public TestBean(String text) {
            this.text = text;
        }
    }
}
