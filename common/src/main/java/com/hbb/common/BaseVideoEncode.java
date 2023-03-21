package com.hbb.common;

import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public abstract class BaseVideoEncode implements IVideoEncode {
    private static final String TAG = "BaseVideoEncode";

    protected String mFlag;    //流标识
    protected IEncodeDataListener mDataListener;    //编码之后回调上层

    protected int mWidth = 0;
    protected int mHeight = 0;
    protected int mFps = 0;
    protected int mBitrate = 0;
    //单次解码时长
    protected int gap = 0;

    protected long generateIndex = 0;
    protected long pts;

    private Disposable filterDisposable;
    private byte[] tempData;

    protected static final ExecutorService executor = Executors.newSingleThreadExecutor();

    protected boolean isSoftware = false;

    protected boolean isStart = false;

    public void saveEncodeInfo(int width, int height, int fps, int bitrate, String flag, IEncodeDataListener dataListener) {
        mWidth = width;
        mHeight = height;
        mFps = fps;
        mBitrate = bitrate;
        mFlag = flag;
        mDataListener = dataListener;
        //单次解码时长
        gap = 1000 / mFps;
        Log.w(TAG, "saveEncodeInfo - gap: " + gap);

        startFrameFilter();

    }

    /**
     * 过滤帧数
     * 避免塞的帧率和编码器帧率不一致的问题
     */
    private void startFrameFilter() {
        filterDisposable = Observable.interval(0, gap, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io()).subscribe(aLong -> {
                    if (tempData != null) {
                        onFrameData(tempData);
                    }
                });
    }

    /**
     * 过滤之后的数据，交给编码器处理
     *
     * @param data
     */
    protected abstract void onFrameData(byte[] data);

    @Override
    public void init(int width, int height, int fps, int bitrate, String flag, IEncodeDataListener dataListener) {
        saveEncodeInfo(width, height, fps, bitrate, flag, dataListener);
    }

    /**
     * 接收上层传来的YUV数据，然后交给定时器做过滤
     *
     * @param data
     */
    @Override
    public void putData(byte[] data) {
        this.tempData = data;
    }


    /**
     * 将编码好的数据回调到应用层处理
     */
    public void callBackApp(byte[] data) {
        int flagIndex = data[4] & 0x1f;
        if (null != mDataListener) {
            if (flagIndex == 7 || flagIndex == 5) {
                mDataListener.onH264Data(data, data.length, true, isSoftware, mFlag);
            } else {
                mDataListener.onH264Data(data, data.length, false, isSoftware, mFlag);
            }
        }
    }


    /**
     * 计算时间戳
     *
     * @return
     */
    protected long computePresentationTime() {
        long value = 132 + generateIndex * 1000000 / mFps;
        generateIndex++;
        return value;
    }


}
