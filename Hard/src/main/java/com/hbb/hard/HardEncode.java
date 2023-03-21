package com.hbb.hard;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;

import com.hbb.common.BaseVideoEncode;
import com.hbb.common.IEncodeDataListener;
import com.hbb.common.utils.ImageUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HardEncode extends BaseVideoEncode {
    private static final String TAG = "AbleEncode";

    private MediaCodec mediaCodec;


    public HardEncode() {
        isSoftware = false;
    }



    @Override
    public void init(int width, int height, int fps, int bitrate, String flag, IEncodeDataListener dataListener) {
        super.init(width, height, fps, bitrate, flag, dataListener);
        initVideoEncode();
    }

    private void initVideoEncode() {
        MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", mWidth, mHeight);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, mBitrate);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, mFps);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 2);
        mediaFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CBR);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);

        try {
            mediaCodec = MediaCodec.createEncoderByType("video/avc");
            mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mediaCodec.start();
            isStart = true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



    //拿到过滤之后数据，去解码
    @Override
    protected void onFrameData(byte[] nv21) {
        if(!isStart){
            return;
        }
        byte[] nv12 = null;
        nv12 = ImageUtil.nv21toNV12(nv21,nv12);
        encodeData(nv12);
    }

    public void encodeData(byte[] inputData) {
        int inputIndex = mediaCodec.dequeueInputBuffer(100);
        if (inputIndex >= 0) {
            pts = computePresentationTime();
            ByteBuffer inputBuffer = mediaCodec.getInputBuffer(inputIndex);
            inputBuffer.clear();
            inputBuffer.put(inputData, 0, inputData.length);
            mediaCodec.queueInputBuffer(inputIndex, 0, inputData.length, pts, 0);

            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int outIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 100);
            while (outIndex >= 0) {
                ByteBuffer outputBuffer = mediaCodec.getOutputBuffer(outIndex);
                byte[] outData = new byte[bufferInfo.size];
                outputBuffer.get(outData);
                callBackApp(outData);
                mediaCodec.releaseOutputBuffer(outIndex, false);
                outIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
            }
        }
    }


    @Override
    public void release() {
        try {
            isStart = false;

            if (mediaCodec!=null) {
                mediaCodec.stop();
                mediaCodec.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
