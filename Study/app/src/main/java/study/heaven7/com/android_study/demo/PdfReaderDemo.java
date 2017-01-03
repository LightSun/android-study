package study.heaven7.com.android_study.demo;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;

import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnDrawListener;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import butterknife.InjectView;
import study.heaven7.com.android_study.BaseActivity;
import study.heaven7.com.android_study.R;

/**
 * Created by heaven7 on 2016/2/25.
 */
public class PdfReaderDemo extends BaseActivity {

    @InjectView(R.id.pdfView)
    PDFView mPdfView;

    private int mPageCount;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private OnDrawListener onDrawListener = new OnDrawListener() {
        @Override
        public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
           // Logger.i(TAG,"onLayerDrawn","");
            int state = canvas.save();

            canvas.translate(100, 100);
            canvas.drawText(displayedPage + "/" + mPageCount, 0, 0, mPaint);

            canvas.restoreToCount(state);
        }
    };
    private OnLoadCompleteListener onLoadCompleteListener = new OnLoadCompleteListener() {
        @Override
        public void loadComplete(int nbPages) {
            mPageCount = nbPages;
        }
    };

    @Override
    protected int getlayoutId() {
        return R.layout.ac_pdf_reader_test;
    }

    @Override
    protected void initView() {
        mPdfView.setSwipeVertical(true);

        mPdfView.fromAsset("yilian_3_1.pdf")
                //.pages(0, 2, 1, 3, 3, 3)
                .defaultPage(1)
                .showMinimap(false)
                .enableSwipe(true)
                .onDraw(onDrawListener)
                .onLoad(onLoadCompleteListener)
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {

                    }
                })
                .load();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }
}
