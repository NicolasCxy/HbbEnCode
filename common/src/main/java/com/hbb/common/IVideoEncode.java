package com.hbb.common;

public interface IVideoEncode {

    void init(int width, int height, int fps, int bitrate, String flag, IEncodeDataListener dataListener);

    void putData(byte[] data);

    void release();

}
