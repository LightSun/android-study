package rapid.decoder.builtin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Build;

import java.io.InputStream;

/**
 * cann't decode , why ???
 * Created by heaven7 on 2016/5/12.
 */
public class ImageDecodeUtil {

    /**
     * @param mime can be null
     * @param in
     * @param region
     * @param opts
     * @param useFilter
     * @param forceRegionToMaxWH force region to match max width and height .if over range
     * @return null, if decode failed.
     */
    public static Bitmap decode(String mime,InputStream in, Rect region, BitmapFactory.Options opts,
                                boolean useFilter,boolean forceRegionToMaxWH){
        Bitmap bitmap = null;
        if(mime == null ){
            bitmap = decode(new JpegDecoder(in),region, opts, useFilter, forceRegionToMaxWH);
            if(bitmap == null){
                bitmap = decode(new PngDecoder(in),region, opts, useFilter, forceRegionToMaxWH);
            }
        }else if(mime.equals("image/jpeg")){
            bitmap = decode(new JpegDecoder(in),region, opts, useFilter, forceRegionToMaxWH);
        }else if(mime.equals("image/png")){
            bitmap = decode(new PngDecoder(in),region, opts, useFilter, forceRegionToMaxWH);
        }
        return bitmap;
    }

    private static Bitmap decode(IImageDecoder d, Rect region,  BitmapFactory.Options opts, boolean useFilter,boolean forceRegionToMaxWH){
        d.initLib();
        try {
            if (!d.begin()) {
                return null;
            }
            if (opts.mCancel) {
                return null;
            }
            //in.startSecondRead();

            final int width = d.getWidth();
            final int height = d.getHeight();

            if(forceRegionToMaxWH){
                if(region.left < 0 || region.top <0 ){
                    throw new IllegalArgumentException();
                }
                if(region.right > width || region.right ==0){
                    region.right = width;
                }
                if(region.bottom > height || region.bottom ==0){
                    region.bottom = height;
                }
            }else {
                validateRegion(region, width, height);
            }

            final Bitmap.Config config = (opts.inPreferredConfig != null ? opts.inPreferredConfig : getDefaultConfig(false));

            return d.decode(region, useFilter, config, opts);
        } finally {
            d.close();
        }
    }

    private static Bitmap decodeJpeg(InputStream in,  Rect region,  BitmapFactory.Options opts, boolean useFilter) {
        JpegDecoder.initDecoder();
        final JpegDecoder d = new JpegDecoder(in);
        try {
            if (!d.begin()) {
                return null;
            }
            if (opts.mCancel) {
                return null;
            }
            //in.startSecondRead();

            final int width = d.getWidth();
            final int height = d.getHeight();

            validateRegion(region, width, height);

            final Bitmap.Config config = (opts.inPreferredConfig != null ? opts.inPreferredConfig : getDefaultConfig(false));

            return d.decode(region, useFilter, config, opts);
        } finally {
            d.close();
        }
    }

    private static Bitmap decodePng(InputStream in,  Rect region,  BitmapFactory.Options opts, boolean useFilter) {
        PngDecoder.initDecoder();
        final PngDecoder d = new PngDecoder(in);
        try {
            if (!d.begin()) {
                return null;
            }

            if (opts.mCancel) return null;

            //in.startSecondRead();

            final int width = d.getWidth();
            final int height = d.getHeight();

            validateRegion(region, width, height);

            final Bitmap.Config config = (opts.inPreferredConfig != null ? opts.inPreferredConfig : getDefaultConfig(false));

            return d.decode(region, useFilter, config, opts);
        } finally {
            d.close();
        }
    }
  //width = maxWidth , height = maxHeight
    private static void validateRegion(Rect region, int width, int height) {
        if (region != null &&
                (region.left < 0 || region.top < 0 || region.right > width || region.bottom > height)) {

            throw new IllegalArgumentException("wrong region");
        }
    }

    private static Bitmap.Config getDefaultConfig(boolean hasAlpha) {
        if (hasAlpha) {
            return Bitmap.Config.ARGB_8888;
        } else {
            return (Build.VERSION.SDK_INT >= 9 ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        }
    }
}
