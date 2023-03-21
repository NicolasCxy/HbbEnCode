//
// Created by DELL on 2022/8/16.
//

#include "VideoChannel.h"
#include <cstring>

#include "x264/armeabi-v7a/include/x264.h"
#include "maniulog.h"

VideoChannel::VideoChannel(JavaCallHelper *mJavaCallHelper) {
    this->javaCallHelper = mJavaCallHelper;
    pthread_mutex_init(&codecMutex, NULL);
}


void VideoChannel::setVideoEncInfo(int width, int height, int fps, int bitrate) {
    mWidth = width;
    mHeight = height;
    mFps = fps;
    mBitrate = bitrate;

    ySize = width * height;
    uvSize = ySize / 4;
    if (videoCodec) {
        x264_encoder_close(videoCodec);
        videoCodec = 0;
    }

    //定义参数
    x264_param_t param;
    x264_param_default_preset(&param, "ultrafast", "zerolatency"); //编码器 速度
    param.i_level_idc = 32;//编码等级
    param.i_csp = X264_CSP_I420;//    选取显示格式
    param.i_width = width;
    param.i_height = height;
    param.i_bframe = 0;//B帧
    param.rc.i_rc_method = X264_RC_ABR; //cpu  ABR 平均
    param.rc.i_bitrate = bitrate / 1024;
    param.i_fps_num = fps;
    param.i_fps_den = 1; //    帧率 时间
    param.i_timebase_den = param.i_fps_num;//    分母
    param.i_timebase_num = param.i_fps_den;//    分子
    param.b_vfr_input = 0;//用fps而不是时间戳来计算帧间距离
    param.i_keyint_max = fps * 2;//I帧间隔
    param.b_repeat_headers = 1;// 是否复制sps和pps放在每个关键帧的前面 该参数设置是让每个关键帧(I帧)都附带sps/pps。
    param.i_threads = 1;//多线程
    x264_param_apply_profile(&param, "baseline");

    videoCodec = x264_encoder_open(&param);


    //容器 - 用来接收原始数
    pic_in = new x264_picture_t;
    x264_picture_alloc(pic_in, X264_CSP_I420, width, height);

}

void VideoChannel::encodeData(int8_t *data) {
    LOGE("上锁！！！");
    pthread_mutex_lock(&codecMutex);
    //填充原始数据到pic_in中
    memcpy(pic_in->img.plane[0], data, ySize);
    for (int i = 0; i < uvSize; i++) {
        *(pic_in->img.plane[2] + i) = *(data + ySize + i * 2);
        //间隔1个字节取一个数据
        //u数据
        *(pic_in->img.plane[1] + i) = *(data + ySize + i * 2 + 1);
    }

    //编出了多少帧
    int pi_nal;
    //编码后的数据
    x264_nal_t *pp_nal;
//编码出的参数  BufferInfo
    x264_picture_t pic_out;

    x264_encoder_encode(videoCodec, &pp_nal, &pi_nal, pic_in, &pic_out);
    //拿数据
//    LOGE("编码出的帧数  %d", pi_nal);
    if (pi_nal > 0) {
        for (int i = 0; i < pi_nal; ++i) {
//            LOGE("输出索引:  %d  输出长度 %d", i, pi_nal);
            javaCallHelper->postH264(reinterpret_cast<char *>(pp_nal[i].p_payload),
                                     pp_nal[i].i_payload, THREAD_CHILD);
        }
    }

    pthread_mutex_unlock(&codecMutex);

}

VideoChannel::~VideoChannel() {
    if (videoCodec) {
        x264_encoder_close(videoCodec);
        videoCodec = 0;
        delete pic_in;
    }

    if (javaCallHelper != nullptr) {
        delete javaCallHelper;
        javaCallHelper = nullptr;
    }
}

