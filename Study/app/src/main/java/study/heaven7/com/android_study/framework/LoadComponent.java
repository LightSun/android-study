package study.heaven7.com.android_study.framework;

/**
 * Created by Administrator on 2017/7/10 0010.
 */

public interface LoadComponent {

    void showLoading(int code);

    void showContent();

    void showError(int code);

    void showEmpty(int code);

    void showTips(int code);
}
