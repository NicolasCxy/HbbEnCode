package com.cxy.hbb_x264;

import android.util.Log;

import com.hbb.common.BaseVideoEncode;
import com.hbb.common.IEncodeDataListener;
import com.hbb.common.NativeCallback;

public class X264Helper extends BaseVideoEncode {
    private static final String TAG = "X264Helper";

    static {
        System.loadLibrary("native-lib");
    }

    private long mx264Pointer  = -1;

    public X264Helper() {
        isSoftware = true;
    }


    /**
     * 底层回调上来编码好的数据
     *
     * @param data
     */
    @NativeCallback
    private void postData(byte[] data) {
        callBackApp(data);
    }


    @Override
    public void init(int width, int height, int fps, int bitrate, String flag, IEncodeDataListener dataListener) {
        super.init(width, height, fps, bitrate, flag, dataListener);
        mx264Pointer = native_int();
        native_setVideoEncInfo(mx264Pointer,width, height, fps, bitrate);
        isStart = true;
    }

    @Override
    protected void onFrameData(byte[] data) {
        if (!isStart) {
            return;
        }
        Log.i(TAG, "onFrameData@: " + data.length + ",flag: " + mFlag + ",name:" + Thread.currentThread().getName());
        pushVideoData(mx264Pointer,data);
    }

    @Override

    public void release() {
        isStart = false;
        native_release(mx264Pointer);
    }


    private native void native_release(long x264Pointer);

    private native long native_int();

    private native void native_setVideoEncInfo(long x264Pointer, int width, int height, int fps, int bitrate);

    private native void pushVideoData(long x264Pointer, byte[] data);


}
