package study.heaven7.com.android_study.demo;

import android.Manifest;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;

import com.heaven7.core.util.Logger;
import com.heaven7.core.util.PermissionHelper;

import java.io.IOException;

/**
 * TextureView with SurfaceTexture test.
 * Created by heaven7 on 2017/5/22 0022.
 */

public class TextureViewTest extends AppCompatActivity implements TextureView.SurfaceTextureListener{

    private static final int RC_CAMERA = 1;
    private final PermissionHelper mHelper = new PermissionHelper(this);

    private Camera mCamera;
    private TextureView mTextureView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTextureView = new TextureView(this);
        mTextureView.setSurfaceTextureListener(this);

        setContentView(mTextureView);

        mHelper.startRequestPermission(new String[]{Manifest.permission.CAMERA},
                new int[]{ RC_CAMERA}, new PermissionHelper.ICallback() {
            @Override
            public void onRequestPermissionResult(String requestPermission, int requestCode, boolean success) {
                 Logger.i("TextureViewTest","onRequestPermissionResult","success = " + success);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mCamera = Camera.open();

        try {
            mCamera.setPreviewTexture(surface);
            mCamera.startPreview();
        } catch (IOException ioe) {
            // Something bad happened
        }
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // Ignored, Camera does all the work for us
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mCamera.stopPreview();
        mCamera.release();
        return true;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Invoked every time there's a new Camera preview frame
    }

}
