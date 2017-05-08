package study.heaven7.com.android_study.demo;

import android.os.Bundle;
import android.widget.Button;

import butterknife.InjectView;
import study.heaven7.com.android_study.BaseActivity;
import study.heaven7.com.android_study.R;
import study.heaven7.com.android_study.help.TagTracker;

/**
 * Created by heaven7 on 2016/4/28.
 */
public class ShowTextActivity extends BaseActivity {

    public static final String KEY_TEXT = "text";
    public static final String KEY_LEVEL = "level";

    @InjectView(R.id.bt)
    Button mBt;

    private String mText;
    private int mLevel = -1;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_show_text;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        String text = getIntent().getStringExtra(KEY_TEXT);
        this.mText = text;
        this.mLevel = getIntent().getIntExtra(KEY_LEVEL , -1);
        if(text!=null) {
            mBt.setText(text);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mText != null){
            TagTracker.getInstance().track(mLevel, mText);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
