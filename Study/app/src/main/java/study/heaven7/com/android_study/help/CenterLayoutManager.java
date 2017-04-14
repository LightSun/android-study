package study.heaven7.com.android_study.help;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.heaven7.core.util.Logger;

import java.util.Locale;

/**
 * center layout to horiental LinearLayoutManager.
 */
public class CenterLayoutManager extends LinearLayoutManager {

    public static final String TAG = "CenterLayoutManager";

    public CenterLayoutManager(Context context) {
        super(context);
    }

    public CenterLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public CenterLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        // super.smoothScrollToPosition(recyclerView, state, position);
        /**
         * like super.
         */
        RecyclerView.SmoothScroller smoothScroller = new CenterSmoothScroller(recyclerView.getContext());
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }

    private static class CenterSmoothScroller extends LinearSmoothScroller {

        CenterSmoothScroller(Context context) {
            super(context);
        }

        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            //return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2);
            String text = String.format(Locale.getDefault(),"(boxStart, viewStart, boxEnd, viewEnd) = (%d, %d, % d, %d )",
                    boxStart, viewStart, boxEnd, viewEnd);
            Logger.i(TAG,"calculateDtToFit","params = " + text);
            Logger.i(TAG,"calculateDtToFit","boxStart , viewStart = " + (boxStart - viewStart));
            switch (snapPreference) {
                case SNAP_TO_START:

                    Logger.i(TAG, "calculateDtToFit", "SNAP_TO_START");
                    return boxStart - viewStart;

                case SNAP_TO_END:
                    Logger.i(TAG, "calculateDtToFit", "SNAP_TO_END");
                    return boxStart - viewStart;

                case SNAP_TO_ANY:
                    Logger.i(TAG, "calculateDtToFit", "SNAP_TO_ANY");
                    return boxStart - viewStart;
                    /*final int dtStart = boxStart - viewStart;
                    if (dtStart > 0) {
                        return dtStart;
                    }
                    final int dtEnd = boxEnd - viewEnd;
                    if (dtEnd < 0) {
                        return dtEnd;
                    }*/

                default:
                    Logger.w(TAG,"calculateDtToFit","default....0");

            }
            return 0;
        }
    }
}

/**
 switch (snapPreference) {
 case SNAP_TO_START:
 return boxStart - viewStart;
 case SNAP_TO_END:
 return boxEnd - viewEnd;
 case SNAP_TO_ANY:
 final int dtStart = boxStart - viewStart;
 if (dtStart > 0) {
 return dtStart;
 }
 final int dtEnd = boxEnd - viewEnd;
 if (dtEnd < 0) {
 return dtEnd;
 */