package com.hbb.encode.helper;

import android.util.Log;

import com.cxy.hbb_x264.X264Helper;
import com.hbb.common.IEncodeDataListener;
import com.hbb.common.IVideoEncode;
import com.hbb.hard.HardEncode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class HbbEncodeHelper {
    private static final String TAG = "HbbEncodeHelper";
    static HbbEncodeHelper mInstance;

    public HashMap<String, IVideoEncode> mVideoEncodeHashMap = new HashMap<>();


    public static HbbEncodeHelper getInstance() {
        if (mInstance == null) {
            synchronized (HbbEncodeHelper.class) {
                if (mInstance == null) {
                    mInstance = new HbbEncodeHelper();
                }
            }

        }
        return mInstance;
    }


    public IVideoEncode makeVideoEncode(int width, int height, int fps, int bitrate, boolean isSoftWare, String flag, IEncodeDataListener dataListener) {
        IVideoEncode videoEncode = null;
        if (isSoftWare) {
            videoEncode = new X264Helper();
        } else {
            videoEncode = new HardEncode();
        }
        videoEncode.init(width, height, fps, bitrate * 1024, flag, dataListener);

        //存起来，后面方便维护
        mVideoEncodeHashMap.put(flag, videoEncode);

        return videoEncode;
    }


    /**
     * 根据标签去找到编码器
     *
     * @param flag
     * @return
     */
    public IVideoEncode findVideoEncodeByFlag(String flag) {
        Log.i(TAG, "findVideoEncodeByFlag: " + flag);
        return mVideoEncodeHashMap.get(flag);
    }


    public void releaseEncodeByFlag(String flag) {
        IVideoEncode videoEncode = mVideoEncodeHashMap.get(flag);
        if (null != videoEncode) {
            videoEncode.release();
        } else {
            Log.w(TAG, "releaseEncodeByFlag -> Lose, the encoder does not exist！");
        }
    }

    /**
     * 释放所有编码器
     */
    public void release() {
        Collection<IVideoEncode> values = mVideoEncodeHashMap.values();
        for (IVideoEncode encode : values) {
            encode.release();
            encode = null;
        }

        mVideoEncodeHashMap.clear();
    }


}
