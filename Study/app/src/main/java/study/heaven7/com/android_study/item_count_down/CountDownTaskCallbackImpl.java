package study.heaven7.com.android_study.item_count_down;

import android.widget.TextView;

import study.heaven7.com.android_study.R;

public abstract class CountDownTaskCallbackImpl<T extends ILeftTimeGetter> implements ICountDownCallback<T> {

    private final TextView mTv;

    public CountDownTaskCallbackImpl(int position, TextView tv) {
        this.mTv = tv;
        tv.setTag(R.id.count_down_position, position);
    }

    @Override
    public void onTick(int pos, T bean, long millisUntilFinished) {
        if (pos == getPosition()) {
            mTv.setText(format(pos,bean, millisUntilFinished));
        }
        //DF.format(new Date(millisUntilFinished)
    }

    private int getPosition() {
        return (int) mTv.getTag(R.id.count_down_position);
    }

    @Override
    public void onFinish(int pos, T bean) {
        if (pos == getPosition()) {
            mTv.setText(format(pos, bean , 0));
            //Logger.i(TAG, "count down time :  finish , pos = " + pos );
        }
    }

    /**
     * format the millisUntilFinished to text, which will set to {@link TextView}
     * @param position the position , often is the position of adapter
     * @param millisUntilFinished the millseconds util finish  or 0 if finished.
     * @param bean the item data
     * @return the formatted text to show.
     */
    protected  abstract CharSequence format( int position,T bean , long millisUntilFinished);
}
