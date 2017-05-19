package com.tt.ttdemoforsdk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tt.CoreType;
import com.tt.SkEgnManager;
import com.tt.adapter.TestWordListAdapter;
import com.tt.entity.TestType;
import com.tt.utils.MyTextWatcher;
import com.tt.widget.audiodialog.AudioRecoderDialog;
import com.tt.widget.audiodialog.AudioRecoderUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Created by cj on 2016/12/26.
 * 英文单词、英文句子、短文朗读评测
 */

public class TestActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener{

    private Toolbar mToolbar;
    private TextView txt_test_content_name;
    private TextView txt_colorful_result;
    private TextView txt_result;
    private EditText mEdt_input_content;
    private Button mBtn_start_test;
    private Button mBtn_replay;
    private LinearLayout rootView;
    private AudioRecoderDialog mRecoderDialog;
    private AudioRecoderUtils mRecoderUtils;

    private ListView mListView;
    private ArrayList<TestType> mTestTypeList = new ArrayList<>();
    private TestWordListAdapter mAdapter;

    private String mTestContent = "";//待测评内容

    private static String mCoreType;
    private static String mQType;
    private static String mTitle;

    public static void gotoTestActivity(Context context, String coreType, String qType, String title) {
        mCoreType = coreType;
        mQType = qType;
        mTitle = title;
        Intent intent = new Intent();
        intent.setClass(context, TestActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_word);
        initView();
        initData();
        initAdapter();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setTitle(mTitle);
        mToolbar.setNavigationIcon(R.drawable.back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txt_test_content_name = (TextView) findViewById(R.id.txt_test_content_name);
        //使textview正常显示音标符号
        Typeface mFace= Typeface.createFromAsset(getAssets(), "font/segoeui.ttf");
        txt_test_content_name.setTypeface(mFace);

        txt_colorful_result = (TextView) findViewById(R.id.txt_colorful_result);
        if(mCoreType.equals(CoreType.EN_SENT_EVAL)){
            txt_colorful_result.setVisibility(View.VISIBLE);
        }else{
            txt_colorful_result.setVisibility(View.GONE);
        }

        txt_result = (TextView) findViewById(R.id.txt_result);
        txt_result.setTypeface(mFace);
        txt_result.setTextIsSelectable(true);
        mListView = (ListView) findViewById(R.id.mListView);
        mEdt_input_content = (EditText) findViewById(R.id.mEdt_input_content);
        mEdt_input_content.addTextChangedListener(new MyTextWatcher(txt_test_content_name, txt_result));
        mBtn_start_test = (Button) findViewById(R.id.mBtn_start_test);
        mBtn_replay = (Button) findViewById(R.id.mBtn_replay);
        mBtn_start_test.setOnTouchListener(this);
        mBtn_replay.setOnClickListener(this);

        rootView = (LinearLayout) findViewById(R.id.rootView);
        //初始化录音时显示的popwindow
        mRecoderUtils = new AudioRecoderUtils();
        mRecoderUtils.setOnAudioStatusUpdateListener(mOnAudioStatusUpdateListener);
        mRecoderDialog = new AudioRecoderDialog(this);
        mRecoderDialog.setShowAlpha(0.98f);
    }

    AudioRecoderUtils.OnAudioStatusUpdateListener mOnAudioStatusUpdateListener = new AudioRecoderUtils.OnAudioStatusUpdateListener() {
        @Override
        public void onUpdate(double db) {
            if (null != mRecoderDialog) {
                mRecoderDialog.setLevel((int) db);
            }
        }
    };

    private void initData() {
        TestType testType;
        if(mCoreType.equals(CoreType.EN_WORD_EVAL)){
            //单词
            testType = new TestType(mCoreType, "hello");
            mTestTypeList.add(testType);
            testType = new TestType(mCoreType, "world");
            mTestTypeList.add(testType);
            testType = new TestType(mCoreType, "happy");
            mTestTypeList.add(testType);
            testType = new TestType(mCoreType, "new");
            mTestTypeList.add(testType);
            testType = new TestType(mCoreType, "year");
            mTestTypeList.add(testType);
            testType = new TestType(mCoreType, "merry");
            mTestTypeList.add(testType);
            testType = new TestType(mCoreType, "christmas");
            mTestTypeList.add(testType);
            testType = new TestType(mCoreType, "activity");
            mTestTypeList.add(testType);
            testType = new TestType(mCoreType, "fragment");
            mTestTypeList.add(testType);
            testType = new TestType(mCoreType, "service");
            mTestTypeList.add(testType);
            testType = new TestType(mCoreType, "broadcast");
            mTestTypeList.add(testType);
            testType = new TestType(mCoreType, "receiver");
            mTestTypeList.add(testType);
            testType = new TestType(mCoreType, "content");
            mTestTypeList.add(testType);
            testType = new TestType(mCoreType, "provider");
            mTestTypeList.add(testType);
            testType = new TestType(mCoreType, "manager");
            mTestTypeList.add(testType);
        }else if(mCoreType.equals(CoreType.EN_SENT_EVAL)){
            //句子
            testType = new TestType(mCoreType, "Did you often play volleyball?");
            mTestTypeList.add(testType);
            testType = new TestType(mCoreType, "When will you go to the club tomorrow?");
            mTestTypeList.add(testType);
            testType = new TestType(mCoreType, "She is going to play volleyball.");
            mTestTypeList.add(testType);
            testType = new TestType(mCoreType, "The number three bus can take us there.");
            mTestTypeList.add(testType);
            testType = new TestType(mCoreType, "She is busy reading books and writing reports at college.");
            mTestTypeList.add(testType);
        }else if(mCoreType.equals(CoreType.EN_PARA_EVAL)){
            //段落
            testType = new TestType(mCoreType, getString(R.string.passage_demo1), mQType);
            mTestTypeList.add(testType);
            testType = new TestType(mCoreType, getString(R.string.passage_demo2), mQType);
            mTestTypeList.add(testType);
        }
        //默认选中第一个元素
        mTestContent = mTestTypeList.get(0).getRefText();
        txt_test_content_name.setText("当前评测内容: "+ mTestContent);
    }

    private void initAdapter() {
        mAdapter = new TestWordListAdapter(this, mTestTypeList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mEdt_input_content.setText("");
                mTestContent = mTestTypeList.get(position).getRefText();
                txt_test_content_name.setText("当前评测内容: "+ mTestTypeList.get(position).getRefText());
                txt_result.setText("");
            }
        });
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                //云端返回结果并做处理
                case SkEgnManager.CODE_RESULT_OK:
                    try {
                        String result = (String)msg.obj;
                        final JSONObject json = new JSONObject(result);
                        Log.e("sss", "返回json===>" + json.toString());

                        String effectiveResult = SkEgnManager.getInstance(TestActivity.this).setResult(json.toString(4), mCoreType);
                        txt_result.setText(effectiveResult);
                        mBtn_start_test.setText(R.string.txt_start_test);

                        if(mCoreType.equals(CoreType.EN_SENT_EVAL)){
                            if(json != null && json.has("result")){
                                setColorfulResult(json.getJSONObject("result"));
                            }
                        }

                        if(mRecoderDialog!=null && mRecoderDialog.isShowing()){
                            mRecoderDialog.dismiss();
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private void setColorfulResult(JSONObject jsonObject) {
        txt_colorful_result.setText("");
        try {
            JSONArray wjsono = jsonObject.getJSONArray("words");
            JSONObject wordJSONObject;
            JSONObject scoreJSONObject;
            SpannableStringBuilder styleWord;

            for(int i=0; i<wjsono.length(); i++){
                wordJSONObject = wjsono.getJSONObject(i);
                scoreJSONObject = wordJSONObject.getJSONObject("scores");

                String word = wordJSONObject.getString("word") + " ";
                int score = scoreJSONObject.getInt("overall");
                styleWord = new SpannableStringBuilder(word);
                if(score < 60){
                    styleWord.setSpan(new ForegroundColorSpan(Color.RED), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }else if(score < 75){
                    styleWord.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }else{
                    styleWord.setSpan(new ForegroundColorSpan(Color.GREEN), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                txt_colorful_result.append(styleWord);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(checkTestWord()){
                    SkEgnManager.getInstance(this).startRecord(mCoreType, mTestContent, mQType, mHandler);
                    mBtn_start_test.setText(R.string.txt_stop_test);
                    mRecoderUtils.startRecord();
                    mRecoderDialog.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                }
                return true;
            case MotionEvent.ACTION_UP:
                SkEgnManager.getInstance(this).stopRecord();
                mBtn_start_test.setText(R.string.txt_start_test);
                mRecoderUtils.stopRecord();
                mRecoderDialog.dismiss();
                return true;
            case MotionEvent.ACTION_CANCEL:
                //此处操作是为了解决android 5.x版本点击评测按钮才跳出权限弹框导致录音动画无法取消问题
                SkEgnManager.getInstance(this).stopRecord();
                mBtn_start_test.setText(R.string.txt_start_test);
                mRecoderUtils.stopRecord();
                mRecoderDialog.dismiss();
                return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mBtn_replay:
                //回放
                SkEgnManager.getInstance(this).playback();
                break;
        }
    }

    //校验评测内容
    private boolean checkTestWord() {
        if(!TextUtils.isEmpty(mEdt_input_content.getText().toString())){
            mTestContent = mEdt_input_content.getText().toString();
        }
        if(TextUtils.isEmpty(mTestContent)){
            Toast.makeText(this, "请输入评测内容", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
