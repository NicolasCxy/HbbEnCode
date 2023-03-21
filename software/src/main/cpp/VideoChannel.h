//
// Created by DELL on 2022/8/16.
//

#ifndef TESTPROJECT_VIDEOCHANNEL_H
#define TESTPROJECT_VIDEOCHANNEL_H

#include <inttypes.h>
#include <jni.h>
#include <sys/types.h>
#include "JavaCallHelper.h"
#include <pthread.h>
#include "x264/armeabi-v7a/include/x264.h">

//x264编码
class VideoChannel {
public:
    VideoChannel(JavaCallHelper *mJavaCallHelper);

    ~VideoChannel();

    void setVideoEncInfo(int width,int height,int fps,int bitrate);

    void encodeData(int8_t *data);

public:
    int mWidth;
    int mHeight;
    int mFps;
    int mBitrate;
    //存放数据容器
    x264_picture_t *pic_in = 0;
    int ySize;
    int uvSize;
    //    编码器
    x264_t *videoCodec = 0;
    //锁对象
    pthread_mutex_t codecMutex;
    //回调类
    JavaCallHelper *javaCallHelper;
};


#endif //TESTPROJECT_VIDEOCHANNEL_H
