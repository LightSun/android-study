package study.heaven7.com.android_study.demo;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.heaven7.core.util.Logger;

import java.util.ArrayList;
import java.util.List;

import study.heaven7.com.android_study.BaseActivity;
import study.heaven7.com.android_study.R;
import study.heaven7.com.android_study.import_object_array.Category;

/**
 * 从资源构造 任意的object array
 * Created by heaven7 on 2017/7/14 0014.
 */

public class AnyObjectArrayFromResTest extends BaseActivity {

    private static final String TAG = "ObjectArray" ;

    @Override
    protected int getLayoutId() {
        return R.layout.item_top_layout_m;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

        List<Category> list = getCategories(this);
        Logger.w(TAG, "initData", list.toString());
    }

    @NonNull
    public List<Category> getCategories(@NonNull Context context) {
        final int DEFAULT_VALUE = 0;
        final int ID_INDEX = 0;
        final int COLOR_INDEX = 1;
        final int LABEL_INDEX = 2;

       // Get the array of objects from the `tasks_categories` array
        final TypedArray statuses = context.getResources().obtainTypedArray(R.array.categories);
        List<Category> categoryList = new ArrayList<>();

        try {
            for (int i = 0; i < statuses.length(); i++) {
                int statusId = statuses.getResourceId(i, DEFAULT_VALUE);
                // Get the properties of one object
                TypedArray rawStatus = context.getResources().obtainTypedArray(statusId);

                Category category = new Category();

                int id = rawStatus.getInteger(ID_INDEX, DEFAULT_VALUE);
                Category.Type categoryId;
                //The ID's should maintain the order with `Category.Type`
                switch (id) {
                    case 0:
                        categoryId = Category.Type.REGISTRATION;
                        break;
                    case 1:
                        categoryId = Category.Type.TO_ACCEPT;
                        break;
                    case 2:
                        categoryId = Category.Type.TO_COMPLETE;
                        break;
                    case 3:
                        categoryId = Category.Type.TO_VERIFY;
                        break;
                    case 4:
                        categoryId = Category.Type.CLOSED;
                        break;
                    default:
                        categoryId = Category.Type.REGISTRATION;
                        break;
                }
                category.setId(categoryId);

                category.setColor(rawStatus.getResourceId(COLOR_INDEX, DEFAULT_VALUE));

                int labelId = rawStatus.getResourceId(LABEL_INDEX, DEFAULT_VALUE);
                category.setName(getString(labelId));

                categoryList.add(category);
                rawStatus.recycle();
            }
        }finally {
            statuses.recycle();
        }
        return categoryList;
    }

}
