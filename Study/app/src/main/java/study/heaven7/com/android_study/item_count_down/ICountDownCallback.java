package study.heaven7.com.android_study.item_count_down;


public interface ICountDownCallback<T extends ILeftTimeGetter> {
        // millisUntilFinished 剩余时间
     void onTick(int position , T bean, long millisUntilFinished);
     void onFinish(int position , T bean);
}
