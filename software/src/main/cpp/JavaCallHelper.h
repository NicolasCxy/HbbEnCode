//
// Created by DELL on 2022/8/16.
//

#ifndef TESTPROJECT_JAVACALLHELPER_H
#define TESTPROJECT_JAVACALLHELPER_H
#include <jni.h>

#define THREAD_MAIN 1
#define THREAD_CHILD 2

class JavaCallHelper {
public:
    JavaCallHelper(JavaVM *_javaVM, JNIEnv *_env, jobject &_jobj);

    void postH264(char *data,int length, int thread = THREAD_MAIN);

    ~JavaCallHelper();

public:
    JavaVM *javaVM;
    JNIEnv *env;
    jobject jobj;
    jmethodID jmid_postData;
    JNIEnv *childThreadEnv;
    JNIEnv *otherJniEnv = NULL;
};


#endif //TESTPROJECT_JAVACALLHELPER_H
