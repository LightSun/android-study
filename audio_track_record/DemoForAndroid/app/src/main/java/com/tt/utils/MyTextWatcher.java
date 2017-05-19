package com.tt.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

/**
 * Created by XLM-10 on 2015/12/2.
 */
public class MyTextWatcher implements TextWatcher {

    TextView mTextView;
    TextView mResultTextView;

    public MyTextWatcher(TextView textView, TextView resultTextView){
        mTextView = textView;
        mResultTextView = resultTextView;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if(editable != null && !"".equals(editable)){
            mTextView.setText("当前评测内容: " + editable.toString());
            mResultTextView.setText("");
        }
    }
}