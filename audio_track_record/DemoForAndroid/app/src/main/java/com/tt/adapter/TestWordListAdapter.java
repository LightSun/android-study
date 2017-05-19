package com.tt.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.tt.entity.TestType;
import com.tt.ttdemoforsdk.R;
import java.util.ArrayList;

/**
 * Created by cj on 2016/12/26.
 */

public class TestWordListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<TestType> mTestTypeList;

    public TestWordListAdapter(Context context, ArrayList<TestType> testTypeList) {
        mContext = context;
        mTestTypeList = testTypeList;
    }

    @Override
    public int getCount() {
        return mTestTypeList.size();
    }

    @Override
    public Object getItem(int position) {
        return mTestTypeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (null == convertView)
        {
            viewHolder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            convertView = mInflater.inflate(R.layout.item_test_word, null);
            viewHolder.txt_word = (TextView) convertView.findViewById(R.id.txt_word);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txt_word.setText(mTestTypeList.get(position).getRefText());
        return convertView;
    }

    private static class ViewHolder
    {
        TextView txt_word;
    }
}
