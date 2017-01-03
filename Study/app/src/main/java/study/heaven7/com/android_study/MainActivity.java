package study.heaven7.com.android_study;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import study.heaven7.com.android_study.demo.DecodeBigImageTestActivity;
import study.heaven7.com.android_study.demo.DragFlowLayoutTest;
import study.heaven7.com.android_study.demo.DragFlowLayoutTest2;
import study.heaven7.com.android_study.demo.DragWindowTest;
import study.heaven7.com.android_study.demo.FileDownloaderDemo;
import study.heaven7.com.android_study.demo.ItemCountDownTest;
import study.heaven7.com.android_study.demo.PdfReaderDemo;
import study.heaven7.com.android_study.demo.SourceTrackTestActivity;
import study.heaven7.com.android_study.demo.TestGraduateActivity;
import study.heaven7.com.android_study.demo.TestNestedScrollActivity;
import study.heaven7.com.android_study.demo.TestPinyinInSearchActivity;
import study.heaven7.com.android_study.demo.TestProgressBar;
import study.heaven7.com.android_study.demo.ViewPagerBigImageTestActivity;

public class MainActivity extends ListActivity {

    public static final String[] options = {
            "FileDownloaderTest",
            "PdfReaderTest",
            "PinyinSearch",
            "SourceTrackTest",
            "DecodeBigImageTest",
            "ViewPagerBigImageTestActivity",
            "DragWindowTest",
            "DragFlowLayoutTest",
            "DragFlowLayoutTest2",
            "ItemCountDownTest",
            "TestProgressBar",
            "TestNestedScrollActivity",
            "TestGraduateActivity",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent;

        switch (position) {
            case 0:
                intent = new Intent(this, FileDownloaderDemo.class);
                break;
            case 1:
                intent = new Intent(this, PdfReaderDemo.class);
                break;
            case 2:
                intent = new Intent(this, TestPinyinInSearchActivity.class);
                break;
            case 3:
                intent = new Intent(this, SourceTrackTestActivity.class);
                break;
            case 4:
                intent = new Intent(this, DecodeBigImageTestActivity.class);
                break;
            case 5:
                intent = new Intent(this, ViewPagerBigImageTestActivity.class);
                break;
            case 6:
                intent = new Intent(this, DragWindowTest.class);
                break;
            case 7:
                intent = new Intent(this, DragFlowLayoutTest.class);
                break;
            case 8:
                intent = new Intent(this, DragFlowLayoutTest2.class);
                break;
            case 9:
                intent = new Intent(this, ItemCountDownTest.class);
                break;

            case 10:
                intent = new Intent(this, TestProgressBar.class);
                break;
            case 11:
                intent = new Intent(this, TestNestedScrollActivity.class);
                break;
            case 12:
                intent = new Intent(this, TestGraduateActivity.class);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        startActivity(intent);
    }
}
