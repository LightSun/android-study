package study.heaven7.com.android_study.demo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import study.heaven7.com.android_study.R;
import study.heaven7.com.android_study.statebar.SystemStatusManager;

/**
 * 沉浸式状态栏.图片的.待测试
 */
public class MainActivity extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_state_bar);
		setTranslucentStatus();
		InitView();
	}

	private void InitView() {
		//标题栏控件
		TextView mTitle = (TextView) findViewById(R.id.tv_title);
		mTitle.setText("默默笙箫");
		TextView mTitleLeftBtn = (TextView) findViewById(R.id.bt_back);
		mTitleLeftBtn.setVisibility(View.VISIBLE);
		mTitleLeftBtn.setOnClickListener(this);
		
	}

	//设置系统状态栏
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setTranslucentStatus() 
	{
		//判断版本是4.4以上
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			Window win = getWindow();
			WindowManager.LayoutParams winParams = win.getAttributes();
			final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
			winParams.flags |= bits;
			win.setAttributes(winParams);
			
			SystemStatusManager tintManager = new SystemStatusManager(this);
			//TODO 打开系统状态栏控制
			//tintManager.setStatusBarTintEnabled(true);
			//tintManager.setStatusBarTintResource(R.drawable.chat_title_bg_repeat);//设置背景
			
			//View layoutAll = findViewById(R.id.layoutAll);
			//设置系统栏需要的内偏移
			//layoutAll.setPadding(0, ScreenUtils.getStatusHeight(this), 0, 0);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_back:
			finish();
			break;

		default:
			break;
		}
		
	}

}
