package study.heaven7.com.android_study.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.heaven7.core.util.Logger;
import com.heaven7.core.util.PermissionHelper;

import butterknife.InjectView;
import butterknife.OnClick;
import study.heaven7.com.android_study.BaseActivity;
import study.heaven7.com.android_study.R;
import study.heaven7.com.android_study.view.GestureMosaicView;

/**
 * Created by heaven7 on 2016/12/27.
 */
public class TestGestureMosaicActivity extends BaseActivity {

    public static final String TAG = "TestGestureMosaicActivity";
    private static final int REQ_PICK_IMAGE = 1984;
    /**
     * 清除
     */
    @InjectView(R.id.bt_clear)
    Button btClear;
    /**
     * 选图
     */
    @InjectView(R.id.bt_load)
    Button btLoad;
    /**
     * 擦除
     */
    @InjectView(R.id.bt_erase)
    Button btErase;
    /**
     * 保存
     */
    @InjectView(R.id.bt_save)
    Button btSave;

    @InjectView(R.id.gesture_mv)
    GestureMosaicView mosaicView;

    private PermissionHelper mHelper;

    @OnClick(R.id.bt_load)
    public void onClickLoad(View v) {
        mHelper.startRequestPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new int[]{5},
                new PermissionHelper.ICallback() {
                    @Override
                    public void onRequestPermissionResult(String requestPermission, int requestCode, boolean success) {
                        if (success) {
                            pickImage();
                        } else {
                            Logger.w("MosaicDemoActivity", "onRequestPermissionResult", "permission failed.");
                        }
                    }
                });
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        String title = getResources().getString(R.string.choose_image);
        Intent chooser = Intent.createChooser(intent, title);
        startActivityForResult(chooser, REQ_PICK_IMAGE);
    }

    @OnClick(R.id.bt_clear)
    public void onClickClear(View v) {
       /* mosaicView.clear();
        mosaicView.setErase(false);*/
    }

    @OnClick(R.id.bt_save)
    public void onClickSave(View view) {
        Toast.makeText(view.getContext(), "onClickSave", Toast.LENGTH_SHORT)
                .show();
    }

    @OnClick(R.id.bt_erase)
    public void onClickRrase(View view) {
        // mosaicView.setErase(true);
    }

    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        // user cancelled
        if (resultCode != Activity.RESULT_OK) {
            Logger.d(TAG, "user cancelled");
            return;
        }

        if (reqCode == REQ_PICK_IMAGE) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            mosaicView.setImageFilePath(filePath);
            //TODO mGestureImage.setImageDrawable(new BitmapDrawable(getResources(), BitmapUtil.getImage(filePath)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected int getlayoutId() {
        return R.layout.ac_gesture_mosaic;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mHelper = new PermissionHelper(this);
    }

}
