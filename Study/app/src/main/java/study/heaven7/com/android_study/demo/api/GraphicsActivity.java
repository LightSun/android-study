package study.heaven7.com.android_study.demo.api;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import study.heaven7.com.android_study.view.PictureLayout;

class GraphicsActivity extends AppCompatActivity {
    // set to true to test Picture
    private static final boolean TEST_PICTURE = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(View view) {
        if (TEST_PICTURE) {
            ViewGroup vg = new PictureLayout(this);
            vg.addView(view);
            view = vg;
        }
        super.setContentView(view);
    }
}
