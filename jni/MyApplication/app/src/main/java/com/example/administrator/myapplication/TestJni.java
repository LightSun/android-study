package com.example.administrator.myapplication;

/**
 * Created by Administrator on 2017/3/8 0008.
 */

public class TestJni {
    static{
        System.loadLibrary("test_jni");
    }

    public static native String getVersion();
}
