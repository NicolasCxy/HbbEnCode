#include <jni.h>
#include <string>

#include "VideoChannel.h"
#include "JavaCallHelper.h"
#include "maniulog.h"

//VideoChannel *videoChannel = NULL;
//JavaCallHelper *javaCallHelper = NULL;

//虚拟机的引用
JavaVM *javaVM = 0;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    javaVM = vm;

    return JNI_VERSION_1_4;

}

extern "C" JNIEXPORT jlong JNICALL
Java_com_cxy_hbb_1x264_X264Helper_native_1int(JNIEnv *env, jobject thiz) {
    JavaCallHelper *javaCallHelper = new JavaCallHelper(javaVM, env, thiz);
    VideoChannel *videoChannel = new VideoChannel(javaCallHelper);

    LOGE("X264_INIT: %p", videoChannel);

    return reinterpret_cast<jlong>(videoChannel);

}
extern "C" JNIEXPORT void JNICALL
Java_com_cxy_hbb_1x264_X264Helper_native_1setVideoEncInfo(JNIEnv *env, jobject thiz,
                                                          jlong x264Client, jint width,
                                                          jint height, jint fps, jint bitrate) {
    VideoChannel *videoChannel = (VideoChannel *) x264Client;
    if (videoChannel) {
        videoChannel->setVideoEncInfo(width, height, fps, bitrate);
    }
}
extern "C" JNIEXPORT void JNICALL
Java_com_cxy_hbb_1x264_X264Helper_pushVideoData(JNIEnv *env, jobject thiz, jlong x264Client,  jbyteArray _data) {

    VideoChannel *videoChannel = (VideoChannel *) x264Client;

    if (videoChannel) {
        LOGE("videoChannel： %p", videoChannel);
        jbyte *data = env->GetByteArrayElements(_data, NULL);
        videoChannel->encodeData(data);
        env->ReleaseByteArrayElements(_data, data, 0);
    }
}
extern "C" JNIEXPORT void JNICALL
Java_com_cxy_hbb_1x264_X264Helper_native_1release(JNIEnv *env, jobject thiz, jlong x264Client) {
    LOGE("Java_com_cxy_hbb_1x264_X264Helper_native_1release");

    VideoChannel *videoChannel = (VideoChannel *) x264Client;

//    if (javaCallHelper != nullptr) {
//        delete javaCallHelper;
//        javaCallHelper = nullptr;
//    }

    if (videoChannel != nullptr) {
        delete videoChannel;
        videoChannel = nullptr;
    }
}