package study.heaven7.com.android_study.framework;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/9 0009.
 */

public class NetRequest {
    //header
    //method
    //map params. file
    //cache
    //callback
    //response: data, file input.
    Map<String, List<String>> mheader;

    interface INetCacher{
        void put(String url, String data);
        String get(String url);
    }

    /**
     * createFormData(String name, String value)
     * createFormData(String name, String filename, File file)
     */
}
