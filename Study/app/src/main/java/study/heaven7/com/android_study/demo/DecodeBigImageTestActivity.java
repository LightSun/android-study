package study.heaven7.com.android_study.demo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.os.AsyncTaskCompat;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

import butterknife.InjectView;
import rapid.decoder.builtin.ImageDecodeUtil;
import study.heaven7.com.android_study.BaseActivity;
import study.heaven7.com.android_study.R;
import study.heaven7.com.android_study.bigimage.ImageViewer;

/**
 * test decode big image or png/jpeg.
 * Created by heaven7 on 2016/5/12.
 */
public class DecodeBigImageTestActivity extends BaseActivity {

    @InjectView(R.id.iv)
    ImageView iv;

    private final Rect mRect = new Rect();
    //private final String mLocalBigImage = "/storage/emulated/0/DCIM/mobile_out/big_image_1.jpg";
    private static final String sLocalBigImage = Environment.getExternalStorageDirectory().getAbsolutePath()
           // +"/DCIM/mobile_out/big_image_1.jpg";
            +"/DCIM/mobile_out/temp.jpg";

    @Override
    protected int getlayoutId() {
        return R.layout.ac_decode_big_image;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

        Intent intent = new Intent(this, ImageViewer.class);
        intent.putExtra(ImageViewer.KEY_IMAGE_PATH, "http://bbsfiles.app111.org/forum/201207/16/2202371jz2friyi2qbbgf6.png");

      /*  Uri uri = Uri.fromFile(new File(sLocalBigImage));
        try {
            Logger.i("mime", "initData","mime = " + getMimeTypeOfUri(this,uri));
        } catch (Exception e) {
            e.printStackTrace();
        }
        intent.putExtra(ImageViewer.KEY_IMAGE_PATH, uri.toString());*/

        startActivity(intent);
        finish();
        //test1();
    }

    private void test1() {
        String url =("http://pub-med-avatar.imgs.medlinker.net/male.png");
        //rapid decoder need rapid decoder jni
        AsyncTaskCompat.executeParallel(new AsyncTask<String,Void,Bitmap>(){
            @Override
            protected Bitmap doInBackground(String... params) {
                try {
                    URL url = new URL(params[0]);
                    final InputStream in = url.openStream();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 1;
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    return ImageDecodeUtil.decode(null, in, mRect, options, false, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                iv.setImageBitmap(bitmap);
            }
        }, url);
    }
    public static String getMimeTypeOfUri(Context context, Uri uri) throws Exception{
        BitmapFactory.Options opt = new BitmapFactory.Options();
    /* The doc says that if inJustDecodeBounds set to true, the decoder
     * will return null (no bitmap), but the out... fields will still be
     * set, allowing the caller to query the bitmap without having to
     * allocate the memory for its pixels. */
        opt.inJustDecodeBounds = true;

        InputStream istream = context.getContentResolver().openInputStream(uri);
        BitmapFactory.decodeStream(istream, null, opt);
        istream.close();
        return opt.outMimeType;
    }
}
