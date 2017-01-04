package study.heaven7.com.android_study.helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heaven7 on 2016/12/29.
 */
public class GroupPointInfo {

    private final List<PointInfo> infos;

    public GroupPointInfo() {
        infos = new ArrayList<>();
    }

    public void addPointInfo(PointInfo info) {
        this.infos.add(info);
    }

    public List<PointInfo> getPointInfos() {
        return infos;
    }

    public void clear() {
        infos.clear();
    }

}
