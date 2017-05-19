package com.tt.ttdemoforsdk;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.tt.AppConfig;
import com.tt.CoreType;
import com.tt.QType;
import com.tt.SkEgnManager;
import com.tt.grant.PermissionsManager;
import com.tt.grant.PermissionsResultAction;
import com.tt.widget.MyProgressDialog;

public class MainActivity extends Activity implements View.OnClickListener{

    private RadioGroup mRadioGroup_engine;
    private RadioButton mRadioButton_cloud;
    private RadioButton mRadioButton_native;

    private Button mBtn_word;
    private Button mBtn_sentence;

    private MyProgressDialog dialog;

    private String mServerType = AppConfig.DEMO_VERSION_CODE == 2? SkEgnManager.SERVER_TYPE_NATIVE : SkEgnManager.SERVER_TYPE_CLOUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("sss", "mServerType===>" + mServerType);
        initView();
        requestAllPermissions();
    }

    private void requestAllPermissions() {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(this,
                new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, new PermissionsResultAction() {

                    @Override
                    public void onGranted() {
                        //注意：针对6.0以上设备，必须在手动获得RECORD_AUDIO权限之后才能初始化引擎
                        //WRITE_EXTERNAL_STORAGE、READ_PHONE_STATE这两个权限是子页面录音评测时本地保存数据时用，可不用在此申请
                        //初始化引擎
                        SkEgnManager.getInstance(MainActivity.this).initEngine(mServerType, mHandler);
                    }

                    @Override
                    public void onDenied(String permission) {

                    }
                });
    }

    private void initView() {
        mRadioGroup_engine = (RadioGroup) findViewById(R.id.mRadioGroup_engine);
        mRadioButton_cloud = (RadioButton) findViewById(R.id.mRadioButton_cloud);
        mRadioButton_native = (RadioButton) findViewById(R.id.mRadioButton_native);

        mBtn_word = (Button) findViewById(R.id.mBtn_word);
        mBtn_sentence = (Button) findViewById(R.id.mBtn_sentence);
        mBtn_word.setOnClickListener(this);
        mBtn_sentence.setOnClickListener(this);
        mRadioGroup_engine.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.mRadioButton_cloud){
                    mServerType = SkEgnManager.SERVER_TYPE_CLOUD;
                }else{
                    mServerType = SkEgnManager.SERVER_TYPE_NATIVE;
                }

                dialog = MyProgressDialog.createDialog(MainActivity.this);
                dialog.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SkEgnManager.getInstance(MainActivity.this).initEngine(mServerType, mHandler);
                    }
                }).start();
            }
        });
        checkDemoVersionCode();
    }

    private void checkDemoVersionCode() {
        switch (AppConfig.DEMO_VERSION_CODE){
            //在线版
            case 1:
                mRadioButton_cloud.setVisibility(View.VISIBLE);
                mRadioButton_cloud.setChecked(true);
                mRadioButton_native.setVisibility(View.GONE);
                break;
            //离线版
            case 2:
                mRadioButton_cloud.setVisibility(View.GONE);
                mRadioButton_native.setVisibility(View.VISIBLE);
                mRadioButton_native.setChecked(true);
                break;
            //在线版&离线版
            case 3:
                mRadioButton_cloud.setVisibility(View.VISIBLE);
                mRadioButton_native.setVisibility(View.VISIBLE);
                break;
        }
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SkEgnManager.STATUS_START_CREATE_ENGINE:
                    Toast.makeText(MainActivity.this, "开始初始化引擎", Toast.LENGTH_SHORT).show();
                    break;
                case SkEgnManager.STATUS_CREATE_ENGINE_SUCCESS:
                    MyProgressDialog.dismissSafe(dialog);
                    Toast.makeText(MainActivity.this, "初始化引擎成功", Toast.LENGTH_SHORT).show();
                    break;
                case SkEgnManager.STATUS_CREATE_ENGINE_FAIL:
                    MyProgressDialog.dismissSafe(dialog);
                    Toast.makeText(MainActivity.this, "初始化引擎失败", Toast.LENGTH_SHORT).show();
                    break;
                case SkEgnManager.STATUS_ENGINE_ALREADY_EXISTS:
                    MyProgressDialog.dismissSafe(dialog);
                    Toast.makeText(MainActivity.this, "引擎已存在", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //引擎回收处理(停止)
        SkEgnManager.getInstance(this).recycle();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mBtn_word:
                //英文单词测评
                TestActivity.gotoTestActivity(this, CoreType.EN_WORD_EVAL, QType.QTYPE_EMPTY, "英文单词测评");
                break;
            case R.id.mBtn_sentence:
                //英文句子测评
                TestActivity.gotoTestActivity(this, CoreType.EN_SENT_EVAL, QType.QTYPE_EMPTY, "英文句子测评");
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }
}
