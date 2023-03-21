package com.hbb.common;

/**
 * 编码数据回调
 */
public interface IEncodeDataListener {

    void onH264Data(byte[] data,int length,boolean isKeyFrame,boolean isSoftware,String flag);
}
