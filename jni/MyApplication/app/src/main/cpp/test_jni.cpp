#include <jni.h>
#include <string>

extern "C"
jstring
Java_com_example_administrator_myapplication_TestJni_getVersion(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "version 1.8.0";
    return env->NewStringUTF(hello.c_str());
}
