//
// Created by DELL on 2022/8/16.
//

#include "JavaCallHelper.h"
#include "maniulog.h"

JavaCallHelper::JavaCallHelper(JavaVM *_javaVM, JNIEnv *_env, jobject &_jobj) : javaVM(_javaVM),
                                                                                env(_env) {
    jobj = env->NewGlobalRef(_jobj);
    jclass jclazz = env->GetObjectClass(jobj);
    jmid_postData = env->GetMethodID(jclazz, "postData", "([B)V");

}

void JavaCallHelper::postH264(char *data, int length, int thread) {
    LOGE("JavaCallHelper - postH264@ : %d, thread@: &d: ", length, thread);
//    jbyteArray array = env->NewByteArray(100);

    //copy数据
//    env->SetByteArrayRegion(array, 0, length, reinterpret_cast<const jbyte *>(data));
//
    if (thread == THREAD_CHILD) {
//        JNIEnv *jniEnv;
//        if (javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK) {
//            return;
//        }

        if (nullptr == otherJniEnv) {
            if (javaVM->AttachCurrentThread(&otherJniEnv, 0) != JNI_OK) {
                if (LOG_DEBUG) {
                    LOGE("call onCallRenderH264 worng");
                }
                return;
            }
        }


        jbyteArray array = otherJniEnv->NewByteArray(length);
        otherJniEnv->SetByteArrayRegion(array, 0, length, reinterpret_cast<const jbyte *>(data));
        otherJniEnv->CallVoidMethod(jobj, jmid_postData, array);
//        javaVM->DetachCurrentThread();
    } else {
//        env->CallVoidMethod(jobj, jmid_postData, array);
    }

//    env->CallVoidMethod(jobj, jmid_postData, array);

}

JavaCallHelper::~JavaCallHelper() {
    LOGE("释放JavaCallHelper: %p", otherJniEnv);

//    if (nullptr != otherJniEnv) {
//        this->javaVM->DetachCurrentThread();
//    }

}


