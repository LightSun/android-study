package rapid.decoder.builtin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * Created by heaven7 on 2016/5/12.
 */
public interface IImageDecoder {

    void initLib();

    void close();

    boolean begin();

    int getWidth();

    int getHeight();

    Bitmap decode(Rect bounds, boolean filter, Bitmap.Config config, BitmapFactory.Options opts);

    boolean hasAlpha();
}
