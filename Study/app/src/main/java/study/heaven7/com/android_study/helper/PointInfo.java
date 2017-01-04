package study.heaven7.com.android_study.helper;

import android.graphics.Point;
import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

public class PointInfo implements Parcelable {

    private PointF point;
    private boolean mStartPoint;

    public PointInfo(float x, float y, boolean mStartPoint) {
        this.point = new PointF(x, y);
        this.mStartPoint = mStartPoint;
    }

    public PointInfo(Point point, boolean mStartPoint) {
        this.point = new PointF(point.x, point.y);
        this.mStartPoint = mStartPoint;
    }

    public void scale(float scale) {
        point.x *= scale;
        point.y *= scale;
    }

    public float getX() {
        return point.x;
    }

    public float getY() {
        return point.y;
    }

    public boolean isStartPoint() {
        return mStartPoint;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.point, 0);
        dest.writeByte(mStartPoint ? (byte) 1 : (byte) 0);
    }

    private PointInfo(Parcel in) {
        this.point = in.readParcelable(PointF.class.getClassLoader());
        this.mStartPoint = in.readByte() != 0;
    }

    public static final Creator<PointInfo> CREATOR = new Creator<PointInfo>() {
        public PointInfo createFromParcel(Parcel source) {
            return new PointInfo(source);
        }

        public PointInfo[] newArray(int size) {
            return new PointInfo[size];
        }
    };
}
